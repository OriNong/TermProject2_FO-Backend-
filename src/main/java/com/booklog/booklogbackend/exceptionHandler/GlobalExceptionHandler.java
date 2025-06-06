package com.booklog.booklogbackend.exceptionHandler;

import com.booklog.booklogbackend.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // JWT 인증 예외 처리
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Object> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("code", ex.getCode());

        return new ResponseEntity<>(body, ex.getStatus());
    }

    // Spring Security 인증 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "인증에 실패했습니다: " + ex.getMessage());
        body.put("code", "AUTHENTICATION_FAILED");

        return ResponseEntity.status(401).body(body);
    }

    // 커스텀 비즈니스 예외 처리
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<Object> handleCustomException(CustomException ex) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("message", ex.getMessage());
//        body.put("code", ex.getCode());
//
//        logger.error("Custom exception: {} - {}", ex.getCode(), ex.getMessage());
//        return ResponseEntity.status(ex.getStatus()).body(body);
//    }

    // 네이버 API 예외 처리
    @ExceptionHandler(NaverApiException.class)
    public ResponseEntity<Object> handleNaverApiException(NaverApiException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "네이버 API 호출 중 오류가 발생했습니다: " + ex.getMessage());

        logger.error("Naver API error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    // 로그인 시 예외 처리
    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<Object> handleUserLoginException(UserLoginException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(body);
    }

    // 회원가입 시 예외 처리
    @ExceptionHandler(UserRegisterException.class)
    public ResponseEntity<Object> handleUserRegisterException(UserRegisterException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(body);
    }

    // 이메일 인증 관련 예외 처리
    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<Object> handleEmailVerificationException(EmailVerificationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(body);
    }

    // NotFount 예외 처리
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // AlreadyExist
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Object> handleAlreadyExistException(AlreadyExistException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // BadRequest
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // @Valid 검증 실패 시 예외 처리
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
//        Map<String, Object> body = new HashMap<>();
//        Map<String, String> errors = new HashMap<>();
//
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//
//        body.put("message", "입력값 검증에 실패했습니다.");
//        body.put("errors", errors);
//        body.put("code", "VALIDATION_FAILED");
//
//        logger.warn("Validation error: {}", errors);
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
//    }

    // 컨트롤러 수행 중 발생한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        Map<String, Object> body = new HashMap<>();

        // 컨트롤러에서 발생한 예외 메시지를 클라이언트에게 반환
        body.put("message", ex.getMessage());
        body.put("code", "SERVER_ERROR");

        // 로깅 추가 (스택 트레이스 포함)
        logger.error("Unexpected error occurred in controller", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}