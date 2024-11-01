package com.cocomo.secondhand_transaction.entity;

import com.cocomo.secondhand_transaction.entity.constant.Authority;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "user")
@Getter
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String nickname;

    private String password;

    private int phone_nb;

    private Authority authority;

}
