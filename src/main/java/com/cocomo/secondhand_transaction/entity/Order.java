package com.cocomo.secondhand_transaction.entity;


import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.dto.ProductDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "`order`")
@Getter
@ToString
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pd_num") // 데이터베이스에서의 컬럼 이름
    private String pdNum;  // 상품 번호, FK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User buyer; // 구매자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User seller; // 판매자

    private boolean payment; // 결제 여부

    private boolean success; // 거래성사 여부

    private int request_cancel; // 취소요청

    private boolean money_delivered; // 대금전달여부

    public Order(Product product, User buyer, User seller) {
        this.pdNum = product.getPdNum();
        this.buyer = buyer;
        this.seller = seller;
        this.payment = false; // 결제 여부 상태 기본값
        this.success = false; // 거래성사 여부 상태 기본값
        this.request_cancel = 0; // 취소 요청 상태 기본값
        this.money_delivered = false; // 대금전달여부 상태 기본값
    }
}
