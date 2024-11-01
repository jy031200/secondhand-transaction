package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.dto.ProductDto;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // 상품 등록 유저 객체 찾기
    private User findUserByAuthentication(Authentication authentication) {
        String email = authentication.getName();  // 현재 로그인한 유저의 인증 정보에서 이메일 추출
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 등록한 지역으로 위도 경도 가져오는 메서드
    private double[] getCoordinatesFromLocation(String location) {
        // TODO (GOOGLE API 로직)
        return new double[]{0, 0}; // 예시
    }

    // 등록된 위도 경도로 지역 가져오는 메서드
    private String getLocationFromCoordinates(double latitude, double longitude) {
        // TODO (GOOGLE API 로직)
        return "서울시 노원구 공릉동";
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
}
