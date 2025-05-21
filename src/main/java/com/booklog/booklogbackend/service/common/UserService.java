package com.booklog.booklogbackend.service.common;

import com.booklog.booklogbackend.Model.request.PasswordChangeRequest;
import com.booklog.booklogbackend.exception.BadRequestException;
import com.booklog.booklogbackend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 비밀번호 일치여부 확인
    public void verifyPassword(Long userId, String inputPassword) {
        String encodedPassword = userMapper.getPasswordById(userId);
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long userId, PasswordChangeRequest request) {
        String currentEncoded = userMapper.getPasswordById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentEncoded)) {
            throw new BadRequestException("현재 비밀번호가 일치하지 않습니다.");
        }

        String newEncoded = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(userId, newEncoded);
    }

    // 닉네임 변경
    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        userMapper.updateNickname(userId, newNickname);
    }
}
