package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.dto.ReviewDto;
import com.cocomo.secondhand_transaction.dto.UserDto;
import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.Review;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.repository.OrderRepository;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.ReviewRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public ReviewService(UserRepository userRepository, ReviewRepository reviewRepository, OrderRepository orderRepository, ProductRepository productRepository1) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository1;
    }

    public User findUserByName(Authentication authentication) {
        String name = authentication.getName();
        return userRepository.findByNickname(name)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }


    // 구매자 정보
/*    private User findUserByAuthentication(Authentication authentication) {
        String email = authentication.getName();  // 현재 로그인한 유저의 인증 정보에서 이메일 추출
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }*/

    // 리뷰 등록
/*    public void registerReview(ReviewDto reviewDto, int od_id, Authentication authentication) {
        Review review = null;

        Order order = getOrderById(od_id); // 상품 ID로 상품 정보 가져오기
        User buyer = findUserByAuthentication(authentication); // 구매자
        User seller = getSellerByOrder(order); // 판매자 이메일

        if (buyer != null && seller != null) {
            review = new Review(reviewDto, buyer, seller, order);
        }

        if (review != null) {
            reviewRepository.save(review);
        } else {
            throw new RuntimeException("리뷰가 등록되지 않았습니다.");
        }
    }*/

/*    // 상품 판매자 정보
    private User getSellerByOrder(Order order) {
        return order.getSeller();
    }

    // 상품 정보 - review는 Order의 id를 참조
    private Order getOrderById(int od_id) {
        return orderRepository.findById(od_id)
                .orElseThrow(() -> new RuntimeException("해당 주문 상품을 찾을 수 없습니다.")); // 적절한 예외 처리
    }*/

        /*public void registerReview(ReviewDto reviewDto, int od_id,Authentication authentication) {
        User buyer = findUserByAuthentication(authentication);
        Order order = getOrderById(od_id);
        User seller = getSellerByOrder(order);

        if (reviewDto.getScore() < 1 || reviewDto.getScore() > 5) {
            throw new IllegalArgumentException("별점은 1에서 5 사이여야 합니다.");
        }

        Review review = new Review(reviewDto, buyer, seller, order);
        reviewRepository.save(review);
    }*/

// 리뷰 등록 메서드 수정
    @Transactional // 데이터베이스 트랜잭션 관리
    public void registerReview(ReviewDto reviewDto, int orderId) {
        Product product = productRepository.findProductByPdName(reviewDto.getPd_name())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        User buyer = order.getBuyer();
        User seller = order.getSeller();

        if (reviewDto.getScore() < 1 || reviewDto.getScore() > 5) {
            throw new IllegalArgumentException("별점은 1에서 5 사이여야 합니다.");
        }

        // Review 객체 생성 및 저장
        Review review = new Review(reviewDto, product, order, buyer, seller);
        reviewRepository.save(review);
    }


    //리뷰 조회
    public List<ReviewDto> showReview(String nickname) {
        User buyer = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("닉네임에 해당하는 구매자를 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findByBuyer(buyer);
        return reviews.stream().map(review -> new ReviewDto(
                        review.getPd_name(),
                        review.getPd_id(),
                        review.getScore(),
                        review.getRv_detail()))
                .collect(Collectors.toList());
    }

    // 판매자에 대해 작성된 리뷰 조회
    public List<ReviewDto> showReviewsBySellerNickname(String sellerNickname) {
        User seller = userRepository.findByNickname(sellerNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 판매자를 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findBySeller(seller);

        List<ReviewDto> reviewss = new ArrayList<>();
        for(Review review : reviews){
            if(review.getSeller().getNickname().equals(sellerNickname)){
                reviewss.add(new ReviewDto(
                        review.getPd_name(),
                        review.getBuyer().getNickname(),
                        review.getScore(),
                        review.getRv_detail()));

/*                return reviews.stream().map(review -> new ReviewDto(
                                review.getPd_name(),
                                review.getBuyer().getNickname(),
                                review.getScore(),
                                review.getRv_detail()))
                        .collect(Collectors.toList());*/
            }
        }
        return reviewss;
    }
}
