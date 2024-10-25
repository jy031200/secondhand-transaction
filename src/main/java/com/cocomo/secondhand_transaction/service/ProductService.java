package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.dto.ProductDto;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.entity.constant.Category;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.api.key}")
    private String apiKey;

    // 1. 위치(주소) -> 위도와 경도로 변환
    public double[] getCoordinatesFromLocation(String location) {
        try {
            // url 인코딩 적용
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedLocation + "&key=" + apiKey;
            Map response = restTemplate.getForObject(url, Map.class);

            // json 응답에서 위도와 경도 추출
            List results = (List) response.get("results");
            if (!results.isEmpty()) {
                Map geometry = (Map) ((Map) results.get(0)).get("geometry");
                Map locationMap = (Map) geometry.get("location");
                double lat = (Double) locationMap.get("lat");
                double lng = (Double) locationMap.get("lng");
                return new double[]{lat, lng};
            } else {
                throw new RuntimeException("위치를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("요청 실패");
        }
    }

    // 2. 위도 경도 -> 위치(주소)로 변환
    public String getLocationFromCoordinates(double latitude, double longitude) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + latitude + "," + longitude + "&key=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        List results = (List) response.get("results");
        if (!results.isEmpty()) {
            return (String) ((Map) results.get(0)).get("formatted_address");
        } else {
            throw new RuntimeException("위치 정보를 찾을 수 없습니다.");
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

        // 3. 위치 없이 위도 경도만 저장 했을 때?
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


    // 상품 수정
    public void updateProduct(String pdNum, ProductDto productDto, Authentication authentication) {
        User user = findUserByAuthentication(authentication);

        Product product = productRepository.findProductByPd_num(pdNum)
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

        Product product = productRepository.findProductByPd_num(pdNum)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (!product.getUser().getId().equals(user.getId())) {
            throw  new RuntimeException("상품을 삭제할 권한이 없습니다");
        }

        productRepository.delete(product);
    }

}
