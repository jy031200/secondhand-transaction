package com.cocomo.secondhand_transaction.controller;

import com.cocomo.secondhand_transaction.dto.ReviewDto;
import com.cocomo.secondhand_transaction.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 등록
/*    @PostMapping
    public ResponseEntity<?> registerReview(
            @RequestBody @Valid ReviewDto reviewDto,
            @RequestParam Integer orderId, // 주문 ID를 요청 파라미터로 받음
            Authentication authentication) {
        reviewService.registerReview(reviewDto, orderId, authentication);
        return ResponseEntity.ok("리뷰가 등록되었습니다.");
    }*/

    @PostMapping("/{orderId}")
    public ResponseEntity<?> registerReview(
            @RequestBody @Valid ReviewDto reviewDto,
            @PathVariable Integer orderId ) {
        reviewService.registerReview(reviewDto, orderId);
        return ResponseEntity.ok("리뷰가 등록되었습니다.");
    }


    // 리뷰 조회
    @GetMapping("/{nickname}")
    public ResponseEntity<List<ReviewDto>> showReviewsByNickname(@PathVariable String nickname, Authentication authentication) {
        String LoginUserNickname = getLoginUserNickname(authentication);
        List<ReviewDto> reviews;

        if(nickname.equals(LoginUserNickname)){
            reviews = reviewService.showReview(LoginUserNickname);
        } else{
            reviews = reviewService.showReviewsBySellerNickname(nickname);
        }

        return ResponseEntity.ok(reviews);
    }

    private String getLoginUserNickname(Authentication authentication) {
        return reviewService.findUserByName(authentication).toString();
    }

}
