package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.components.MailComponents;
import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.repository.OrderRepository;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.ReviewRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import com.cocomo.secondhand_transaction.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService implements OrderServiceImpl {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final MailComponents mailComponents;

    // 구매자 정보
    private User findUserByAuthentication(Authentication authentication) {
        String email = authentication.getName();  // 현재 로그인한 유저의 인증 정보에서 이메일 추출
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 상품 판매자 정보
    private User getSellerByOrder(Product product) {
        return product.getUser();
    }

    // 상품 정보 - Product의 pd_num을 참조
    private Product getProductById(String pd_num) {
        return productRepository.findProductByPdNum(pd_num)
                .orElseThrow(() -> new RuntimeException("해당 주문 상품을 찾을 수 없습니다.")); // 적절한 예외 처리
    }

    // 주문 등록
    @Transactional // 트랜잭션 관리
    public void registerOrder(String pd_num, Authentication authentication) {
        Product product = getProductById(pd_num); // 상품 ID로 상품 정보 가져오기
        User buyer = findUserByAuthentication(authentication); // 구매자
        User seller = getSellerByOrder(product); // 판매자

        Order order = new Order(product, buyer, seller);
        orderRepository.save(order);
    }
}

