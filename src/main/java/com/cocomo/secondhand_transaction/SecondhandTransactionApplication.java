package com.cocomo.secondhand_transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SecondhandTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondhandTransactionApplication.class, args);
    }

}
