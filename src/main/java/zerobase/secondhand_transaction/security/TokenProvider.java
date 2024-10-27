package zerobase.secondhand_transaction.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.secondhand_transaction.entity.constant.Authority;


import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 5;

    @Value("${jwt.secret}")
    private String secretKey;

    // jwt 의 세 부분 : header, payload, signature
    // header : 토큰의 타입 (jwt) 과 사용된 서명 알고리즘 (HMAC SHA256) 지정
    // payload : 토큰의 실제 데이터 포함 <- 여기에 claims 포함
    // signature : 토큰의 무결성 검증 위해 사용. 헤더와 페이로드를 합쳐서 비밀 키로 서명
    // 토큰 생성 메서드
    public String generateToken(String email, Authority authority) {
        var now = new Date(); // 현재 시간
        var expiration = new Date(now.getTime() + TOKEN_EXPIRE_TIME); // 만료 시간

        // 클레임 생성 및 설정
        Claims claims = Jwts.claims()
                .setSubject(email);
        claims.put(KEY_ROLES, authority);
        /*
         Claim -> jwt 토큰의 페이로드에 포함될 데이터를 담는 객체
         헤더랑 시그니처는 ? -> jwt 라이브러리에서 자동으로 생성해줌
         => Jwts.builder() 호출시 자동으로 헤더 생성
         => signWith() ~ 에서 서명 생성
         */

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }


    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("Token is empty");
            return false;
        }
        try {
            var claims = parseClaims(token);
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token is expired");
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Invalid token : {}", e.getMessage());
            return false;
        }
    }

    // 클레임 객체 추출 및 토큰 유효성 검사
    public Claims parseClaims(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            return Jwts.parserBuilder() // jwt 토큰을 파싱, 검증하기 위한 파서 객체를 생성하는 빌더 객체 초기화
                    .setSigningKey(key) // 파서에 서명 검증에 사용할 서명 키 설정
                    .build() // jwt 토큰을 파싱하는 jwt 토큰 파서 객체 생성 => 토큰 검증 준비 완료
                    .parseClaimsJws(token) // 주어진 jwt 토큰을 앞서 만든 파서로 파싱하여 클레임 추출 => 서명과 만료시간 모두 검증
                    .getBody();
        } catch (ExpiredJwtException e) { // 만료된 토큰인 경우
            log.error("Expired jwt token : {}", e.getMessage());
            return e.getClaims(); // 만료된 토큰이어도 클레임 객체 반환
        } catch (io.jsonwebtoken.MalformedJwtException e) { // 잘못된 형식의 토큰인 경우
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid token format");
        } catch (io.jsonwebtoken.SignatureException e) { // 잘못된 서명인 경우
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new RuntimeException("Invalid token signature");
        } catch (IllegalArgumentException e) { // 잘못된 인자 입력인 경우
            log.error("Illegal argument: {}", e.getMessage());
            throw new RuntimeException("Illegal argument provided for token");
        }
    }
}