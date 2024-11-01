package com.cocomo.secondhand_transaction.controller;

import com.cocomo.secondhand_transaction.dto.ProductDto;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.constant.Category;
import com.cocomo.secondhand_transaction.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 상품 등록
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerProduct(
            @RequestBody @Valid ProductDto productDto,
            Authentication authentication) {
        // 로그인한 사용자 정보로 상품 등록
        productService.registerProduct(productDto, authentication);
        return ResponseEntity.ok("상품이 등록되었습니다.");
    }

    // 상품 조회 - 1. 기본 조건으로 필터링 검색
    @GetMapping
    public ResponseEntity<?> searchProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String pdNum) {
        // 다양한 조건으로 상품을 검색
        List<Product> products = productService.searchProduct(name, category, location, nickname, pdNum);
        return ResponseEntity.ok(products);
    }

    // 상품 조회 - 2. 사용자의 위치 기반 조회 (반경 5km 내의 상품들만 출력)
//    @GetMapping("/mylocation")
//    public ResponseEntity<?> searchWithLocation() {
//
//    }


    // 상품 수정
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update/{pdNum}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String pdNum,
            @RequestBody ProductDto productDto,
            Authentication authentication) {
        productService.updateProduct(pdNum, productDto, authentication);
        return ResponseEntity.ok("상품이 수정되었습니다.");
    }

    // 상품 삭제
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/delete/{pdNum}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable String pdNum,
            Authentication authentication) {
        productService.deleteProduct(pdNum, authentication);
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }

}
