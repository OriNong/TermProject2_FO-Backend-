package com.booklog.booklogbackend.config;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // 사용자 이메일로 JWT 토큰 생성
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration((new Date(System.currentTimeMillis() + accessTokenExpiration)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // 고유 식별자로 사용자 refreshToken 생성
    public String generateRefreshToken() {
        String refreshTokenID = UUID.randomUUID().toString();                           // 고유 식별자 생성
        return Jwts.builder()
                .setId(refreshTokenID)                                                  // UUID를 페이로드에 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getTokenIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch ( JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
