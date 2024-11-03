package com.cocomo.secondhand_transaction.entity;

import jakarta.persistence.*;
        import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "order")
@Getter
@ToString
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String odNum; // 주문 번호 (중복 없음) (이 번호로 주문 구별)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer; // 구매자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pd_num", nullable = false) // 외래키로 연결, 데이터베이스에서의 컬럼 이름
    private Product product; // Product 엔티티와 관계 설정

    @Column(nullable = false)
    private boolean payment; // 결제여부 / 기본값: false (구매자가 결제완료하면 true, 상품 상태(status?)는 예약중으로)

    @Column(nullable = false)
    private boolean success; // 거래성사여부 / 기본값: false (결제여부 true + 거래 후 구매자 확인 시 => true)

    @Column(nullable = false)
    private Integer request_cancel; // 취소요청 / 기본값: 0
    //          구매자가 취소 요청 보낼 시: 1
    //          판매자의 요청 승인: 2
    //          요청 거절 시: -1
    @Column(nullable = false)
    private boolean money_delivery; // 대금전달여부 / 기본값: false ( 거래성사여부가 true 일 때 대금이 전달 되고 그 후 true)

    // 상품 등록번호 (중복x)
    public String generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 생성자
    public Order(Product product, User buyer, User seller) {
        this.product = product;
        this.buyer = buyer;
        this.seller = seller;
        this.payment = false; // 결제 여부 상태 기본값
        this.success = false; // 거래 성공 여부 상태 기본값
        this.request_cancel = 0; // 취소 요청 상태 기본값
        this.money_delivery = false; // 대금 전달 여부 상태 기본값
        this.odNum = generateOrderNumber();
    }
}
