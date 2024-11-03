package com.cocomo.secondhand_transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    @NotNull
    private String pd_name; // 상품 명(Product의 pd_name)

    @NotNull
    private int pd_id; // 주문 번호

    @NotNull
    private String buyerNickname; // 구매자 닉네임

    @NotNull
    private int score; // 리뷰 점수

    private String rv_detail; // 리뷰 내용

    public ReviewDto(String pd_name, int pd_id, int score, String rv_detail) {
        this.pd_name = pd_name;
        this.pd_id = pd_id;
        this.score = score;
        this.rv_detail = rv_detail;
    }


    public ReviewDto(String pd_name, String nickname, int score, String rv_detail) {
        this.pd_name = pd_name;
        this.buyerNickname = nickname;
        this.score = score;
        this.rv_detail = rv_detail;
    }
}
