package com.cocomo.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Authority {
    ROLE_ADMIN,
    ROLE_USER;

    // 사용자 문자열 입력을 대문자로 변환 후 Authority enum 객체로 변환
    @JsonCreator
    public static Authority fromString(String value) {
        return Authority.valueOf(value.toUpperCase());
    }
}
