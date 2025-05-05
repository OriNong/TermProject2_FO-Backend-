package com.booklog.booklogbackend.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    // redis에 저장 시 key-value 형식
    private static final String EMAIL_VERIFICATION_PREFIX = "email:verify:";

    // 인증 코드 만료 시간 (5분)
    private static final int CODE_TTL = 5;

    /**
     * 인증 코드 발송
     * @param email: 회원가입하려는 이메일
     */
    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        try {
            // 메일 발송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("BookLog 회원가입 인증 코드");
            message.setText("코드: " + code + "\n5분 내 입력하세요.");
            mailSender.send(message);

            // Redis에 저장 (5분 유효)
            redisTemplate.opsForValue().set(EMAIL_VERIFICATION_PREFIX + email, code, CODE_TTL, TimeUnit.MINUTES);

            log.info("인증 코드 발송 및 Redis 저장 완료 - 이메일: {}", email);
        } catch (Exception e) {
            log.error("이메일 발송 실패 - 이메일: {}, 에러: {}", email, e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    /**
     * 발송된 이메일 코드와 사용자 입력 인증 코드 일치 여부 확인
     * @param email: 인증 코드 수신 이메일 주소
     * @param inputCode: 사용자 입력 인증 코드
     * @return
     */
    public boolean verifyCode(String email, String inputCode) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            log.warn("인증 코드 만료 또는 존재하지 않음 - 이메일: {}", email);
            return false; // 코드 만료
        }

        if (storedCode.equals(inputCode)) {
            // 인증 성공 시 Redis에서 삭제
            redisTemplate.delete(key);
            log.info("이메일 인증 성공 - 이메일: {}", email);
            return true;
        } else {
            log.warn("인증 코드 불일치 - 이메일: {}, 입력 코드: {}", email, inputCode);
            return false;
        }
    }

    /**
     * 이메일 인증 코드 생성
     * @return: 무작위 6자리 숫자 형식 문자열 반환
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}
