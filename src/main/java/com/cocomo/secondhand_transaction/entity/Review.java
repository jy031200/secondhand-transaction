package com.cocomo.secondhand_transaction.entity;


import com.cocomo.secondhand_transaction.dto.ReviewDto;
import jakarta.persistence.*;
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

    private int pd_id; // 주문 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User buyer; // 리뷰 작성자(구매자) (1:M)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User seller; // 판매자 (1:1)

    private Integer score; // 별점

    private String rv_detail; // 상세 설명

    //생성자 - 리뷰 등록
    public Review(ReviewDto reviewDto, Product product, Order order, User buyer, User seller) {
        this.pd_name = product.getPd_name();
        this.pd_id = order.getId();
        this.buyer = buyer;
        this.seller = seller;
        this.score = reviewDto.getScore();
        this.rv_detail = reviewDto.getRv_detail();
    }
}
