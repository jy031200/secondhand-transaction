package com.cocomo.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderCancelRequestStatus {
    NONE, // 기본값
    REQUESTED, // 요청됨
    APPROVED, // 승인됨
    REJECTED; // 거절됨

    @JsonCreator
    public static OrderCancelRequestStatus fromString(String value) {
        return OrderCancelRequestStatus.valueOf(value.toUpperCase());
    }
}