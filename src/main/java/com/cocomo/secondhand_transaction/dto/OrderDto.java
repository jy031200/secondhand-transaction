package com.cocomo.secondhand_transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class OrderDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order { // 거래 요청 할 때 들어가는 dto
        private String pdNum;
        private String selectedTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class responseOrder {
        private String pdNum;
        private String productName;
        private String sellerNickname;
        private String buyerNickname;
        private String selectedTime;
        private String status;
    }

}
