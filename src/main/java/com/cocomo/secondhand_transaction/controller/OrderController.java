package com.cocomo.secondhand_transaction.controller;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.dto.ProductDto;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.service.OrderService;
import com.cocomo.secondhand_transaction.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 상품 주문 등록
    @PostMapping
    public ResponseEntity<?> registerOrder(
            @RequestParam String pd_num, // pd_num을 RequestParam으로 받기
            Authentication authentication) {
        orderService.registerOrder(pd_num, authentication);
        return ResponseEntity.ok("상품이 등록되었습니다.");
    }
}
