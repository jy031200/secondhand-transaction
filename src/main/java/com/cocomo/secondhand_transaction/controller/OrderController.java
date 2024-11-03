package com.cocomo.secondhand_transaction.controller;

import com.cocomo.secondhand_transaction.dto.OrderDto;
import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 거래 요청
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/request")
    public ResponseEntity<?> requestOrder(
            @RequestBody OrderDto.Order orderDto,
            Authentication authentication) {
        orderService.requestOrder(orderDto, authentication);
        return ResponseEntity.ok("거래가 요청 되었습니다. 거래 승인까지 기다려주세요.");
    }

    // 거래 요청 승인 (판매자가) + 바로 결제
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{orderId}/approve")
    public ResponseEntity<?> approveOrder(
            @PathVariable String orderId,
            Authentication authentication) {
        orderService.approveOrder(orderId, authentication);
        return ResponseEntity.ok("거래가 승인 되었습니다. 이메일을 확인해주세요.");
    }

    // 거래 요청 거절 (판매자가)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{orderId}/reject")
    public ResponseEntity<?> rejectOrder(
            @PathVariable String orderId,
            Authentication authentication) {
        orderService.rejectOrder(orderId, authentication);
        return ResponseEntity.ok("거래 승인이 거절되었습니다.");
    }

    // 거래 취소 요청 (구매자가)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable String orderId,
            Authentication
                    authentication) {
        orderService.cancelOrder(orderId, authentication);
        return ResponseEntity.ok("거래 취소가 요청 되었습니다.");
    }

    // 거래 취소 요청 승인 (판매자가)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel/approve/{orderId}")
    public ResponseEntity<?> approveCancel(
            @PathVariable String orderId,
            Authentication authentication) {
        orderService.approveCancel(orderId, authentication);
        return ResponseEntity.ok("거래가 취소 되었습니다.");
    }

    // 거래 취소 요청 거절 (판매자가)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/cancel/reject/{orderId}")
    public ResponseEntity<?> rejectCancel(
            @PathVariable String orderId,
            Authentication authentication) {
        orderService.rejectCancel(orderId, authentication);
        return ResponseEntity.ok("거래 취소가 거절되었습니다.");
    }

    // 거래 확정 (구매자가)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<?> confirmOrder(
            @PathVariable String orderId,
            Authentication authentication) {
        orderService.confirmOrder(orderId, authentication);
        return ResponseEntity.ok("거래가 확정되었습니다.");
    }

    // 내 거래 조회
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/myorder")
    public ResponseEntity<?> getMyOrder(Authentication authentication) {
        List<OrderDto.responseOrder> orders = orderService.getMyOrder(authentication);
        return ResponseEntity.ok(orders);
    }
}
