package zerobase.secondhand_transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import zerobase.secondhand_transaction.dto.OrderDto;


@Entity
@Table(name = "Orders")
@Getter
@ToString
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pd_num")
    private Product pd_num;

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

    //생성자
    public Order(OrderDto orderDto, User user, Product product) {
        this.pd_num = product;
        this.seller = user;
//        this.buyer = user;
        this.payment = false;
        this.request_cancel = 0;
        this.success = false;
        this.money_delivery = false;
    }
}