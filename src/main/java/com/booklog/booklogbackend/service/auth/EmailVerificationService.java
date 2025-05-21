package com.booklog.booklogbackend.service.auth;

import com.booklog.booklogbackend.exception.EmailVerificationException;
import com.booklog.booklogbackend.exception.NotFoundException;
import com.booklog.booklogbackend.mapper.UserMapper;
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


    /**
     * < Redis 키 PREFIX >
     */
    // 회원 가입 시 이메일 인증
    private static final String EMAIL_VERIFICATION_PREFIX = "email:verify:";
    // 비밀번호 재설정 시 인증코드 저장
    private static final String PASSWORD_RESET_PREFIX = "password:reset:";
    // 비밀번호 재설정 인증 완료 플래그
    private static final String PASSWORD_VERIFIED_PREFIX = "password:reset:verified:";

    /**
     * TTL
     */
    // 인증 완료 유효 시간 (30분 동안 유효) -> 인증 완료 후 인증 정보 저장
    private static final int VERIFIED_FLAG_TTL = 30;
    // 인증 코드 만료 시간 (5분)
    private static final int CODE_TTL = 5;
    private final UserMapper userMapper;

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
            throw new EmailVerificationException("이메일 발송에 실패했습니다.");
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
            throw new EmailVerificationException("인증 코드 만료 또는 존재하지 않음");
        }

        if (storedCode.equals(inputCode)) {
            // 인증 성공 시 Redis에서 삭제
            redisTemplate.delete(key);
            log.info("이메일 인증 성공 - 이메일: {}", email);

            // 인증 완료 플래그 설정 (30분 유효)
            String verifiedKey = EMAIL_VERIFICATION_PREFIX + "verified:" + email;
            redisTemplate.opsForValue().set(verifiedKey, "true", VERIFIED_FLAG_TTL, TimeUnit.MINUTES);
            log.info("이메일 인증 내역 30분간 유효 - 이메일: {}", email);
            return true;
        } else {
            throw new EmailVerificationException("인증 코드가 일치하지 않습니다");
        }
    }

    /**
     * 비밀번호 재설정 인증코드 발송
     * @param email : 사용자 가입 시 입력한 이메일
     */
    public void sendPasswordResetCode(String email) {
        if (!userMapper.existsByEmail(email)) {
            throw new NotFoundException("이메일을 다시 확인하세요");
        }
        String code = generateVerificationCode();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("BookLog 비밀번호 재설정 인증 코드");
            message.setText("코드: " + code + "\n5분 내 입력하세요.");
            mailSender.send(message);

            redisTemplate.opsForValue().set(PASSWORD_RESET_PREFIX + email, code, CODE_TTL, TimeUnit.MINUTES);
            log.info("비밀번호 재설정 코드 발송 및 Redis 저장 완료 - 이메일: {}", email);
        } catch (Exception e) {
            log.error("비밀번호 재설정 이메일 발송 실패 - 이메일: {}, 에러: {}", email, e.getMessage(), e);
            throw new EmailVerificationException("이메일 발송에 실패했습니다.");
        }
    }

    /**
     * 발송된 비밀번호 재설정 인증코드와 사용자 입력 인증코드 값 일치 여부 확인
     * @param email : 이메일
     * @param inputCode : 사용자 입력 인증코드
     */
    public void verifyPasswordResetCode(String email, String inputCode) {
        String key = PASSWORD_RESET_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new EmailVerificationException("인증 코드 만료 또는 존재하지 않음");
        }

        if (storedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set(PASSWORD_VERIFIED_PREFIX + email, "true", VERIFIED_FLAG_TTL, TimeUnit.MINUTES);
            log.info("비밀번호 재설정 인증 성공 및 유효 플래그 저장 - 이메일: {}", email);
        } else {
            throw new EmailVerificationException("인증 코드가 일치하지 않습니다");
        }
    }

    /**
     * 비밀번호 재설정을 위해 인증코드 일치 여부 확인
     * @return : 인증 완료 여부
     */
    public boolean isPasswordResetVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get(PASSWORD_VERIFIED_PREFIX + email));
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
