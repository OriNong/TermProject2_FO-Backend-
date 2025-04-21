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
    public void register(UserVO userVO) {
        log.debug("Registering user: {}", userVO.getEmail());
        try {
            boolean exists = userMapper.existsByEmail(userVO.getEmail());
            log.debug("Email exists: {} -> {}", userVO.getEmail(), exists);
            if (exists) {
                log.debug("Email already exists: {}", userVO.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
            userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
            log.debug("Inserting user: {}", userVO.getEmail());
            userMapper.insertUser(userVO);
            log.debug("User inserted: {}", userVO.getEmail());
        } catch (Exception e) {
            log.error("Error in register for email {}: {}", userVO.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, String> login(String email, String password) {
        log.debug("Logging in user: {}", email);
        try {
            UserVO userVO = userMapper.findByEmail(email);
            if (userVO == null || !passwordEncoder.matches(password, userVO.getPassword())) {
                log.debug("Invalid email or password: {}", email);
                throw new IllegalArgumentException("Invalid email or password");
            }

            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken(email);

            refreshTokenMapper.deleteByUserId(userVO.getId());
            refreshTokenMapper.save(userVO.getId(), refreshToken, new Date(System.currentTimeMillis() + 604800000));

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
    if (!jwtTokenProvider.validateToken(refreshToken)) {
        throw new IllegalArgumentException("Invalid refresh token!");
    }

    String email = jwtTokenProvider.getEmailFromToken(refreshToken);
    UserVO userVO = userMapper.findByEmail(email);
    if (userVO == null) {
        throw new IllegalArgumentException("User not found!");
    }

    String storedToken = refreshTokenMapper.findByuserId(userVO.getId());
    if( !refreshToken.equals(storedToken)) {
        throw new IllegalArgumentException("Refresh token mismatch!");
    }
    return jwtTokenProvider.generateAccessToken(email);
    }

    public void logout(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }
}

