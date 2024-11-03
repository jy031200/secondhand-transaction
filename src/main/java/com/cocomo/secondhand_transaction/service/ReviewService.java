package com.cocomo.secondhand_transaction.service;

import com.cocomo.secondhand_transaction.dto.ReviewDto;
import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.Review;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.repository.OrderRepository;
import com.cocomo.secondhand_transaction.repository.ProductRepository;
import com.cocomo.secondhand_transaction.repository.ReviewRepository;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(UserRepository userRepository, ReviewRepository reviewRepository, OrderRepository orderRepository, ProductRepository productRepository1) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    public User findUserByName(Authentication authentication) {
        String name = authentication.getName();
        return userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 리뷰 등록 메서드
    @Transactional // 데이터베이스 트랜잭션 관리
    public void registerReview(ReviewDto reviewDto, String orderId) {
        Order order = orderRepository.findByOrderNum(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        if (!order.isSuccess()) {
            throw new RuntimeException("거래가 종료되지 않았습니다.");
        }
        User buyer = order.getBuyer();
        User seller = order.getSeller();
        Product product = order.getProduct();

        // Review 객체 생성 및 저장
        Review review = new Review(reviewDto, product, orderId, buyer, seller);
        reviewRepository.save(review);
    }


    //리뷰 조회
    public List<ReviewDto> showReview(String nickname) {
        User buyer = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("닉네임에 해당하는 구매자를 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findByBuyer(buyer);
        return reviews.stream().map(review -> new ReviewDto(
                        review.getPd_name(),
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
                        review.getScore(),
                        review.getRv_detail()));
            }
        }
        return reviewss;
    }

    // 리뷰 삭제
    public void deleteReview(String orderNum, Authentication authentication) {
        User user = findUserByName(authentication);
        Review review = reviewRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if (!review.getBuyer().equals(user)) {
            throw new RuntimeException("리뷰 삭제 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }

}