package com.cocomo.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RequestOrder {
    REQUESTED, // 기본값, // 요청됨
    APPROVED, // 승인됨
    REJECTED, // 거절됨
    NONE; // 취소 시

    @JsonCreator
    public static RequestOrder fromString(String value) {
        return RequestOrder.valueOf(value.toUpperCase());
    }
}
