package com.cocomo.secondhand_transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @NotNull
    private String password;

    @NotNull
    private int phone_nb;
}
