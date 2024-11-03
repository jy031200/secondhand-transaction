package com.cocomo.secondhand_transaction.entity;

import com.cocomo.secondhand_transaction.dto.ReviewDto;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "review")
@Getter
@ToString
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String pd_name;

    private String orderNum; // 주문 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer; // 리뷰 작성자(구매자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller; // 판매자

    private Integer score; // 별점(최소 1점 최대 5점)

    private String rv_detail; // 상세 설명

    //생성자 - 리뷰 등록
    public Review(ReviewDto reviewDto, Product product, String orderNum, User buyer, User seller) {
        this.pd_name = product.getPd_name();
        this.orderNum = orderNum;
        this.buyer = buyer;
        this.seller = seller;
        this.score = reviewDto.getScore();
        this.rv_detail = reviewDto.getRv_detail();
    }
}