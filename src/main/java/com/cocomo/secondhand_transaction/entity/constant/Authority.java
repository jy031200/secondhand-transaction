package com.cocomo.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Authority {
    USER,  // 유저
    ADMIN; // 권한

    @JsonCreator
    public static Authority fromString(String value) {
        return Authority.valueOf(value.toUpperCase());
    }

}
