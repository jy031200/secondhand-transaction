package com.cocomo.secondhand_transaction.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    @NotNull
    private String pd_name; // 상품 명(Product의 pd_name)

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score; // 리뷰 점수

    private String rv_detail; // 리뷰 내용

}