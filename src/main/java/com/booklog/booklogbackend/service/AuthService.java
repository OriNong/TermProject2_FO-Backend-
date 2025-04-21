package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.vo.Users;
import com.booklog.booklogbackend.config.JwtTokenProvider;
import com.booklog.booklogbackend.mapper.RefreshTokenMapper;
import com.booklog.booklogbackend.mapper.UsersMapper;
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
    private final UsersMapper usersMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UsersMapper usersMapper, RefreshTokenMapper refreshTokenMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usersMapper = usersMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void register(Users user) {
        log.debug("Registering user: {}", user.getEmail());
        try {
            boolean exists = usersMapper.existsByEmail(user.getEmail());
            log.debug("Email exists: {} -> {}", user.getEmail(), exists);
            if (exists) {
                log.debug("Email already exists: {}", user.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Inserting user: {}", user.getEmail());
            usersMapper.insertUser(user);
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
            Users user = usersMapper.findByEmail(email);
            if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                log.debug("Invalid email or password: {}", email);
                throw new IllegalArgumentException("Invalid email or password");
            }

            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken(email);

            refreshTokenMapper.deleteByUserId(user.getId());
            refreshTokenMapper.save(user.getId(), refreshToken, new Date(System.currentTimeMillis() + 604800000));

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
    Users user = usersMapper.findByEmail(email);
    if (user == null) {
        throw new IllegalArgumentException("User not found!");
    }

    String storedToken = refreshTokenMapper.findByuserId(user.getId());
    if( !refreshToken.equals(storedToken)) {
        throw new IllegalArgumentException("Refresh token mismatch!");
    }
    return jwtTokenProvider.generateAccessToken(email);
    }

    public void logout(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }
}

