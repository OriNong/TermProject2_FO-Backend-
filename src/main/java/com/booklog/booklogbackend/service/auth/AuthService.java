package com.booklog.booklogbackend.service.auth;

import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.config.JwtTokenProvider;
import com.booklog.booklogbackend.mapper.RefreshTokenMapper;
import com.booklog.booklogbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
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

    public AuthService(UserMapper userMapper, RefreshTokenMapper refreshTokenMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void register(UserVO user) {
        log.debug("Registering user: {}", user.getEmail());
        try {
            boolean existsByEmail = userMapper.existsByEmail(user.getEmail());
            log.debug("Email exists: {} -> {}", user.getEmail(), existsByEmail);
            if (existsByEmail) {
                log.debug("Email already exists: {}", user.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
            boolean existsByNickname = userMapper.existsByNickname(user.getNickname());
            log.debug("Nickname exists: {} -> {}", user.getNickname(), existsByNickname);
            if (existsByNickname) {
                log.debug("Nickname already exists: {}", user.getNickname());
                throw new IllegalArgumentException("Nickname already exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Inserting user: {}", user.getEmail());
            userMapper.insertUser(user);
            log.debug("User inserted: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error in register for email {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, String> login(String email, String password) {
        log.debug("Logging in user: {}", email);
        try {
            UserVO loginUser = userMapper.findByEmail(email);
            if (loginUser == null || !passwordEncoder.matches(password, loginUser.getPassword())) {
                log.debug("Invalid email or password: {}", email);
                throw new IllegalArgumentException("Invalid email or password");
            }

            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken();
            String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);

            refreshTokenMapper.deleteByUserId(loginUser.getUserId());
            refreshTokenMapper.save(loginUser.getUserId(), tokenId, refreshToken, new Date(System.currentTimeMillis() + 604800000));

            log.debug("Login successful for email: {}", email);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        } catch (Exception e) {
            log.error("Error in login for email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to login: " + e.getMessage(), e);
        }
    }

    public String refreshToken(String refreshToken) {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.debug("Invalid refresh token");
                throw new IllegalArgumentException("Invalid refresh token");
            }

            String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);
            String storedToken = refreshTokenMapper.findTokenByTokenId(tokenId);
            if (storedToken == null || !refreshToken.equals(storedToken)) {
                log.debug("Refresh token mismatch or not found for tokenId: {}", tokenId);
                throw new IllegalArgumentException("Refresh token mismatch");
            }

            UserVO user = userMapper.findByTokenId(tokenId); // 새 메서드 필요
            if (user == null) {
                log.debug("User not found for tokenId: {}", tokenId);
                throw new IllegalArgumentException("User not found");
            }

            return jwtTokenProvider.generateAccessToken(user.getEmail());
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh token: " + e.getMessage(), e);
        }
    }

    public void logout(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }

    public boolean emailAlreadyExists(String email) {
        return userMapper.existsByEmail(email);
    }

    public boolean nicknameAlreadyExists(String nickname) {
        return userMapper.existsByNickname(nickname);
    }
}

