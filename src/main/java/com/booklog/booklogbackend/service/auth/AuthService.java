package com.booklog.booklogbackend.service.auth;

import com.booklog.booklogbackend.Model.VerificationStatus;
import com.booklog.booklogbackend.Model.response.AccessTokenRefreshResponse;
import com.booklog.booklogbackend.Model.response.LoginSuccessResponse;
import com.booklog.booklogbackend.Model.response.UserProfileResponse;
import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.config.JwtTokenProvider;
import com.booklog.booklogbackend.exception.AlreadyExistException;
import com.booklog.booklogbackend.exception.UserLoginException;
import com.booklog.booklogbackend.exception.UserRegisterException;
import com.booklog.booklogbackend.mapper.RefreshTokenMapper;
import com.booklog.booklogbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public AuthService(UserMapper userMapper, RefreshTokenMapper refreshTokenMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, StringRedisTemplate redisTemplate) {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    // 이메일 인증 확인을 위한 키 접두어 (EmailVerificationService와 동일한 접두어 사용)
    private static final String EMAIL_VERIFICATION_PREFIX = "email:verify:";

    /**
     * 사용자 회원 가입
     * @param user : 회원가입 시 입력한 사용자 정보
     */
    @Transactional
    public void register(UserVO user) {
        log.debug("Registering user: {}", user.getEmail());
        try {
            boolean existsByEmail = userMapper.existsByEmail(user.getEmail());
            log.debug("Email exists: {} -> {}", user.getEmail(), existsByEmail);
            if (existsByEmail) {
                log.debug("Email already exists: {}", user.getEmail());
                throw new AlreadyExistException("이미 사용중인 이메일입니다.");
            }
            boolean existsByNickname = userMapper.existsByNickname(user.getNickname());
            log.debug("Nickname exists: {} -> {}", user.getNickname(), existsByNickname);
            if (existsByNickname) {
                log.debug("Nickname already exists: {}", user.getNickname());
                throw new AlreadyExistException("이미 사용중인 닉네임입니다.");
            }

            // 이메일 인증 확인
            if (!isEmailVerified(user.getEmail())) {
                log.debug("Email not verified: {}", user.getEmail());
                throw new IllegalArgumentException("Email verification required");
            }

            // 비밀번호 암호화
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 이메일 인증 상태 설정
            user.setIsVerified(VerificationStatus.VERIFIED);

            log.debug("Inserting user: {}", user.getEmail());
            userMapper.insertUser(user);
            log.debug("User inserted: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error in register for email {}: {}", user.getEmail(), e.getMessage(), e);
            throw new UserRegisterException(user.getEmail(), e.getMessage());
        }
    }

    /**
     * 이메일 인증 여부 확인
     * Redis에 해당 이메일의 인증 이력이 있는지 확인
     * 참고: 이메일 검증은 EmailVerificationService에서 수행되며, 성공 시 Redis에서 삭제됨
     * 따라서 추가적인 검증 플래그가 필요함
     */
    private boolean isEmailVerified(String email) {
        // 검증 완료 플래그 키 (이메일 검증 성공 시 설정됨)
        String verifiedKey = EMAIL_VERIFICATION_PREFIX + "verified:" + email;
        Boolean isVerified = redisTemplate.hasKey(verifiedKey);
        return Boolean.TRUE.equals(isVerified);
    }

    /**
     * 사용자 로그인
     * 요청된 이메일과 비밀번호를 서버 저장 값과 비교
     * @return : access token/refresh token 발급, nickname 반환
     */
    @Transactional
    public LoginSuccessResponse login(String email, String password) {
        log.debug("Logging in user: {}", email);
        try {
            UserVO loginUser = userMapper.findByEmail(email);
            if (loginUser == null || !passwordEncoder.matches(password, loginUser.getPassword())) {
                log.debug("Invalid email or password: {}", email);
                throw new UserLoginException("유효하지 않은 이메일 또는 비밀번호입니다.");
            }

            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken();
            String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);

            refreshTokenMapper.deleteByUserId(loginUser.getUserId());
            refreshTokenMapper.save(loginUser.getUserId(), tokenId, refreshToken, new Date(System.currentTimeMillis() + 604800000));

            log.debug("Login successful for email: {}", email);
            return new LoginSuccessResponse(accessToken, refreshToken, loginUser.getNickname());
        } catch (Exception e) {
            log.error("Error in login for email {}: {}", email, e.getMessage(), e);
            throw new UserLoginException("유효하지 않은 이메일 또는 비밀번호입니다.");
        }
    }

    /**
     * 사용자 프로필 리턴
     * @return : email, nickname, createdAt, updatedAt
     */
    public UserProfileResponse getUserProfile(Long userId) {
        try{
            UserProfileResponse loginUser = userMapper.findByUserId(userId);
            if (loginUser == null) {
                throw new IllegalArgumentException("User not found");
            }
            return loginUser;
        } catch (Exception e) {
            log.error("Error in findByUserId {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find User: " + e.getMessage(), e);
        }
    }

    /**
     * 클라이언트 요청에서 access token이 만료된 경우
     * 클라이언트의 refresh token과 서버 내 refresh token 값 비교하여
     * access token 재발급
     */
    public String refreshToken(String refreshToken) {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.debug("Invalid refresh token");
                String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);
                refreshTokenMapper.deleteByTokenId(tokenId); // 로그아웃 처리
                throw new IllegalArgumentException("Invalid refresh token");
            }

            String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);
            String storedToken = refreshTokenMapper.findTokenByTokenId(tokenId);
            if (storedToken == null || !refreshToken.equals(storedToken)) {
                log.debug("Refresh token mismatch or not found for tokenId: {}", tokenId);
                refreshTokenMapper.deleteByTokenId(tokenId); // 로그아웃 처리
                throw new IllegalArgumentException("Refresh token mismatch");
            }

            UserVO user = userMapper.findByTokenId(tokenId); // 사용자 조회
            if (user == null) {
                log.debug("User not found for tokenId: {}", tokenId);
                refreshTokenMapper.deleteByTokenId(tokenId); // 로그아웃 처리
                throw new IllegalArgumentException("User not found");
            }

            return jwtTokenProvider.generateAccessToken(user.getEmail());
        } catch (IllegalArgumentException e) {
            throw e; // 상위 컨트롤러에서 처리
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh token: " + e.getMessage(), e);
        }
    }

    //  리프레시 토큰이 잘못된 경우 로그아웃 처리
    public void logoutByToken(String refreshToken) {
        try {
            String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);
            refreshTokenMapper.deleteByTokenId(tokenId);
            log.info("Logged out by tokenId: {}", tokenId);
        } catch (Exception e) {
            log.warn("Failed to delete token during logout: {}", e.getMessage());
            // 무시 가능
        }
    }

    // 로그아웃 처리
    public void logout(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }

    // 이메일 중복 체크
    public boolean emailAlreadyExists(String email) {
        return userMapper.existsByEmail(email);
    }

    // 닉네임 중복 체크
    public boolean nicknameAlreadyExists(String nickname) {
        return userMapper.existsByNickname(nickname);
    }
}

