package com.cocomo.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Payment {
    NONE, // 기본값, // 요청됨
    DEPOSITED, // 입금 확인
    REFUND; // 환불됨

    @JsonCreator
    public static Payment fromString(String value) {
        return Payment.valueOf(value.toUpperCase());
    }
}
