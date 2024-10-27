package zerobase.secondhand_transaction.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zerobase.secondhand_transaction.dto.UserDto;
import zerobase.secondhand_transaction.service.UserService;

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
        // 현재 로그인한 클라이언트의 요청정보 (HttpServletRequest) 에서 jwt 토큰 추출
        // 유효성 검사는 필터에서 이미 하고 넘어옴
        String token = request.getHeader("Authorization").substring(7); // "Bearer " 제거

        userService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}
