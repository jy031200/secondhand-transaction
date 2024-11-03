package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.dto.ProductDto;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.entity.constant.Category;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import com.cocomo.secondhand_transaction.util.Haversine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Value("${google.api.key}")
    private String apiKey;

    // 1. 위치(주소) -> 위도와 경도로 변환
    // 위치(주소) -> 위도와 경도로 변환
    public double[] getCoordinatesFromLocation(String location) {
        log.debug("Entering getCoordinatesFromLocation with location: {}", location);
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedLocation + "&key=" + apiKey;

        try {
            // URL 생성 및 연결 설정
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 응답 확인 및 데이터 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // 연결 종료 및 데이터 로그
            in.close();
            log.debug("Geocoding API JSON response (HttpURLConnection): {}", content);

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(content.toString(), Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results != null && !results.isEmpty()) {
                Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
                Map<String, Double> locationMap = (Map<String, Double>) geometry.get("location");
                double lat = locationMap.get("lat");
                double lng = locationMap.get("lng");
                return new double[]{lat, lng};
            } else {
                log.warn("No results found for location: {}", location);
                throw new RuntimeException("위치를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching coordinates for location: {}", location, e);
            throw new RuntimeException("요청 실패");
        }
    }




    // 2. 위도 경도 -> 위치(주소)로 변환
    public String getLocationFromCoordinates(double latitude, double longitude) {
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + latitude + "," + longitude + "&key=" + apiKey + "&language=ko";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(response.toString());

            JsonNode results = responseJson.get("results");
            if (results != null && results.size() > 0) {
                return results.get(0).get("formatted_address").asText();
            } else {
                throw new RuntimeException("위치 정보를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            throw new RuntimeException("요청 실패", e);
        }
    }


    // 상품 등록 유저 객체 찾기
    private User findUserByAuthentication(Authentication authentication) {
        String email = authentication.getName();  // 현재 로그인한 유저의 인증 정보에서 이메일 추출
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }


    // 상품 등록
    public void registerProduct(ProductDto productDto, Authentication authentication) {
        Product product = null;
        // 유저 객체 찾기
        User user = findUserByAuthentication(authentication);

        // 1. 위치, 위도, 경도 모두 정확히 입력 했을 때
        if (productDto.getLocation() != null && productDto.getLatitude() != null && productDto.getLongitude() != null) {
            product = new Product(productDto, user);
        }


        // 2. 위치를 ~시 ~동으로만 입력 했을 때 -> 이 위치의 대략적인 위도 경도를 db에 따로 저장해야 함
        if (productDto.getLocation() != null && productDto.getLatitude() == null && productDto.getLongitude() == null) {
            double[] coordinates = getCoordinatesFromLocation(productDto.getLocation());
            double latitude = coordinates[0]; // 가져온 위도
            double longitude = coordinates[1]; // 가져온 경도
            product = new Product(productDto, user, latitude, longitude);
        }

        // 3. 위치 없이 위도 경도만 저장 했을 때
        if (productDto.getLocation() == null && productDto.getLatitude() != null && productDto.getLongitude() != null) {
            String location = getLocationFromCoordinates(productDto.getLatitude(), productDto.getLongitude());
            product = new Product(productDto, user, location);
        }

        if (product != null) {
            productRepository.save(product);
        } else {
            throw new RuntimeException("상품 등록 정보가 충분하지 않습니다.");
        }
    }

    // 상품 조회 - 1. 기본 조건으로 필터링 조회 (동적 조건 처리)
    public List<Product> searchProduct(String name, Category category, String location, String nickname, String pdNum) {
        // 초기 Specification 객체 생성
        Specification<Product> spec = Specification.where(null);

        // 조건 1. 상품 번호로 조회 (특정 상품 클릭해서 조회)
        if (pdNum != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("pd_num"), pdNum));
            return productRepository.findAll(spec);
        }

        // 그 외 ... 동적 조건 처리
        // 상품명 조건
        if (name != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("pd_name"), "%" + name + "%"));
        }

        // 카테고리 조건 추가
        if (category != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), category));
        }

        // 위치 조건 추가
        if (location != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("location"), "%" + location + "%"));
        } // 여기 람다식에선 단일 표현식을 사용했기 때문에 return 따로 X


        // 판매자의 닉네임 조건 추가
        if (nickname != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                // User 엔터티와 조인하여 nickname 필터링 조건 추가
                Join<Product, User> userJoin = root.join("user", JoinType.INNER);
                return criteriaBuilder.equal(userJoin.get("nickname"), nickname);
            });
        } // 여기의 람다식에선 단일 표현법이 아니라 코드 블록 (노션에 정리해둠)
        // 리턴이 searchProduct 메서드의 리턴이 아니라 람다식의 리턴일뿐임!


        // 모든 조건이 결합된 Specification 을 통해 쿼리 실행
        return productRepository.findAll(spec);
    }

    // 상품 조회 - 2. 사용자 위치 기반
    public List<Product> searchWithLocation(Double latitude, Double longitude) {
        double distance = 5.0; // 항상 5km로 설정

        List<Product> allProducts = productRepository.findAll(); // 모든 상품 조회

        // 반경 내의 상품 필터링
        return allProducts.stream()
                .filter(product -> {
                    double productDistance = Haversine.calculateDistance(
                            latitude, longitude, product.getLatitude(), product.getLongitude());
                    return productDistance <= distance; // 5km 이내의 상품만 필터링
                })
                .collect(Collectors.toList());
    }


    // 상품 수정
    public void updateProduct(String pdNum, ProductDto productDto, Authentication authentication) {
        User user = findUserByAuthentication(authentication);

        Product product = productRepository.findProductByPdNum(pdNum)
                .orElseThrow(() ->new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        // 상품 등록자와 유저가 올바른지 확인
        if (!product.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("상품을 수정할 권한이 없습니다");
        }

        // 위치가 수정된 경우 -> 위도/경도 자동 갱신
        if (productDto.getLocation() != null && !productDto.getLocation().equals(product.getLocation())) {
            double[] coordinates = getCoordinatesFromLocation(productDto.getLocation());
            product.updateProductInfo(productDto, product.getLocation(), coordinates[0], coordinates[1]);
        } else if (productDto.getLatitude() != null && productDto.getLongitude() != null) {
            String location = getLocationFromCoordinates(productDto.getLatitude(), productDto.getLongitude());
            product.updateProductInfo(productDto, location, product.getLatitude(), product.getLongitude());
        }
    }

    // 상품 삭제
    public void deleteProduct(String pdNum, Authentication authentication) {
        User user = findUserByAuthentication(authentication);

        Product product = productRepository.findProductByPdNum(pdNum)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (!product.getUser().getId().equals(user.getId())) {
            throw  new RuntimeException("상품을 삭제할 권한이 없습니다");
        }
        productRepository.delete(product);
    }

}
