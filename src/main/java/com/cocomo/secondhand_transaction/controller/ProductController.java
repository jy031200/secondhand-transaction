package com.cocomo.secondhand_transaction.controller;

import com.cocomo.secondhand_transaction.dto.ProductDto;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.constant.Category;
import com.cocomo.secondhand_transaction.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 상품 등록
    @PostMapping
    public ResponseEntity<?> registerProduct(
            @RequestBody @Valid ProductDto productDto,
            Authentication authentication) {
        // 로그인한 사용자 정보로 상품 등록
        productService.registerProduct(productDto, authentication);
        return ResponseEntity.ok("상품이 등록되었습니다.");
    }

 /*   // 상품 검색
    @GetMapping
    public ResponseEntity<?> searchProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String pdNum
    ) {
        // 다양한 조건으로 상품을 검색
        List<Product> products = productService.searchProducts(name, category, location, nickname, pdNum);
        return ResponseEntity.ok(products);
    }*/


    // 상품 수정

    // 상품 삭제


}
