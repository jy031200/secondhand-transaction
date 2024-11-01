package com.cocomo.secondhand_transaction.dto;

import com.cocomo.secondhand_transaction.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NotNull
    private User buyer; // 구매자

    @NotNull
    private User seller; // 판매자

}
