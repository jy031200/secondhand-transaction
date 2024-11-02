package com.cocomo.secondhand_transaction.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cocomo.secondhand_transaction.dto.UserDto;
import com.cocomo.secondhand_transaction.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid UserDto.SignUp request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDto.Login request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }

    // 로그아웃
    @PostMapping("/user_logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        userService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

}