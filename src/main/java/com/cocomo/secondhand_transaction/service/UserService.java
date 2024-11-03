package com.cocomo.secondhand_transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cocomo.secondhand_transaction.dto.UserDto;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.repository.UserRepository;
import com.cocomo.secondhand_transaction.security.TokenProvider;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + email));

    }

    // 회원가입
    public void register(UserDto.SignUp userDto){
        boolean exists = userRepository.existsByEmail(userDto.getEmail());
        if (exists) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        String encodingPassword = passwordEncoder.encode(userDto.getPassword());
        User user = new User(userDto.getEmail(), userDto.getNickname(), encodingPassword, userDto.getPhone_nb(), userDto.getAuthority());
        userRepository.save(user);
    }

    public String login(UserDto.Login userDto) {
        // 1. 사용자 인증
        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(userDto.getEmail() + "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // 2. 이미 로그인된 사용자인지 확인
        String existingToken = (String) redisTemplate.opsForValue().get("JWT:" + user.getEmail());
        if (existingToken != null) {
            throw new RuntimeException("이미 로그인된 유저입니다.");
        }

        // 3. JWT 토큰 생성 및 Redis 저장
        String token = tokenProvider.generateToken(user.getEmail(), user.getAuthority());
        redisTemplate.opsForValue().set("JWT:" + user.getEmail(), token, 5, TimeUnit.HOURS);

        return token; // JWT 토큰 반환
    }

    // 로그아웃
    public void logout(String token) {
        String email = tokenProvider.getEmail(token);
        redisTemplate.delete("JWT:" + email);
    }
}