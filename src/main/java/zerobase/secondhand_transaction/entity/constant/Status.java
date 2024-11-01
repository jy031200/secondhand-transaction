package zerobase.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    AVAILABLE, // 구매 가능
    RESERVED, // 예약중 (결제 완료, 거래 확정 전)
    SOLD_OUT; // 거래 완료

    @JsonCreator
    public static Status fromString(String value) {
        return Status.valueOf(value.toUpperCase());
    }
}