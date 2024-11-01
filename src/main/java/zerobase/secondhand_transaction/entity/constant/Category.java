package zerobase.secondhand_transaction.entity.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    CLOTHING,        // 의류
    ELECTRONICS,     // 가전제품
    DIGITAL_DEVICE,  // 디지털기기
    COSMETICS,       // 화장품
    OTHER;            // 기타

    @JsonCreator
    public static Category fromString(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}