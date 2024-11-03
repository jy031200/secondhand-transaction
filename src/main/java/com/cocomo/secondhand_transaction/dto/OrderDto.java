package com.cocomo.secondhand_transaction.dto;

import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private boolean payment; // 결제여부

    private boolean success; // 거래성사여부

    private Integer request_cancel; // 취소요청

    private boolean money_delivery; // 대금전달여부

    private Product product;

    @NotNull
    private User buyer; // 구매자

    @NotNull
    private User seller; // 판매자



}