package com.cocomo.secondhand_transaction.entity;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.entity.constant.OrderCancelRequestStatus;
import com.cocomo.secondhand_transaction.entity.constant.Payment;
import com.cocomo.secondhand_transaction.entity.constant.RequestOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.cocomo.secondhand_transaction.entity.constant.Status.RESERVED;

@Entity
@Table(name = "Orders")
@Getter
@ToString
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pd_num", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String selectedTime; // 선택한 거래 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestOrder requestOrder; // 거래 요청 승인 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Payment payment; // 결제여부 / 기본값: NONE

    @Column(nullable = false)
    private boolean success; // 거래성사여부 / 기본값: false -> true가 되면 대금 전달한다고 한번에 묶어서 가정

    @Column(nullable = false)
    private boolean notified; // 거래 확정 요청 알림

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderCancelRequestStatus requestCancel; // 거래 취소 요청

    @Column(nullable = false)
    private String orderNum; // 주문 번호

    @Column(nullable = false)
    private LocalDateTime createdDt;

    @PrePersist
    public void prePersist(){
        this.createdDt = LocalDateTime.now(); // DB 저장 직전? 시간 설정
    }

    // 주문 번호 (중복 X)
    public String generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 생성자
    public Order (OrderDto.Order order, User seller, User buyer, Product product) {
        this.seller = seller;
        this.buyer = buyer;
        this.product = product;
        product.updateProductStatus(RESERVED);
        this.selectedTime = order.getSelectedTime();
        this.requestOrder = RequestOrder.REQUESTED;
        this.payment = Payment.NONE;
        this.success = false;
        this.notified = false;
        this.requestCancel = OrderCancelRequestStatus.NONE;
        this.orderNum = generateOrderNumber();
    }

    // 거래 승인 여부
    public void updateRequestOrder(RequestOrder requestOrder) {
        this.requestOrder = requestOrder;
    }

    // 결제 여부
    public void updatePayment(Payment payment) {
        this.payment = payment;
    }

    // 거래 취소 요청, 승인, 거절 ..
    public void updateRequestCancelOrder(OrderCancelRequestStatus requestCancel) {
        this.requestCancel = requestCancel;
    }


    // 거래 확정 시
    public void orderSuccess() {
        this.success = true;
    }

    // 거래 확정 요청
    public void orderNotified() {
        this.notified = true;
    }
}