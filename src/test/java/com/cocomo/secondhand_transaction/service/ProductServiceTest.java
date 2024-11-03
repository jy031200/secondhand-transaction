package com.cocomo.secondhand_transaction.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void getCoordinatesFromLocation() {
        String address = "서울특별시 노원구 공릉동";
        double[] coordinates = productService.getCoordinatesFromLocation(address);

        System.out.println("위도: " + coordinates[0] + ", 경도: " + coordinates[1]);

        // 예상한 위도와 경도를 기준으로 테스트 확인 (예시 좌표를 넣어 비교)
        assertEquals(37.620082, coordinates[0], 0.01); // 약간의 오차 허용
        assertEquals(127.075722, coordinates[1], 0.01);
    }

    @Test
    public void testGetLocationFromCoordinates() {
        double latitude = 37.620082;
        double longitude = 127.075722;
        String location = productService.getLocationFromCoordinates(latitude, longitude);

        System.out.println("주소: " + location);

        // 주소 확인
        assertTrue(location.contains("서울특별시 노원구 공릉동"));
    }
}