package com.booklog.booklogbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtAuthenticationException extends RuntimeException {
  private final HttpStatus status;
  private final String code;

  public JwtAuthenticationException(String message, HttpStatus status, String code) {
    super(message);
    this.status = status;
    this.code = code;
  }

    // 토큰 만료 예외
  public static JwtAuthenticationException expired() {
    return new JwtAuthenticationException(
            "토큰이 만료되었습니다. 다시 로그인하세요.",
            HttpStatus.UNAUTHORIZED,
            "TOKEN_EXPIRED"
    );
  }

  // 유효하지 않은 토큰 예외
  public static JwtAuthenticationException invalid() {
    return new JwtAuthenticationException(
            "유효하지 않은 토큰입니다.",
            HttpStatus.UNAUTHORIZED,
            "INVALID_TOKEN"
    );
  }

  // 토큰 없음 예외
  public static JwtAuthenticationException missing() {
    return new JwtAuthenticationException(
            "인증 토큰이 필요합니다.",
            HttpStatus.UNAUTHORIZED,
            "TOKEN_MISSING"
    );
  }

  // 인증 실패 일반 예외 (간소화된 버전)
  public static JwtAuthenticationException authenticationFailed() {
    return new JwtAuthenticationException(
            "인증에 실패했습니다.",
            HttpStatus.UNAUTHORIZED,
            "AUTHENTICATION_FAILED"
    );
  }
}
