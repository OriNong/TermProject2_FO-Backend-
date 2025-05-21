package com.booklog.booklogbackend.controller.common;

import com.booklog.booklogbackend.Model.request.NicknameUpdateRequest;
import com.booklog.booklogbackend.Model.request.PasswordChangeRequest;
import com.booklog.booklogbackend.Model.request.PasswordVerifyRequest;
import com.booklog.booklogbackend.service.common.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 비밀번호 일치 여부 확인
     */
    @PostMapping("/pw/verify")
    public ResponseEntity<Void> verifyPassword(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody PasswordVerifyRequest request
    ) {
        userService.verifyPassword(userId, request.getPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 변경
     * @param userId : 로그인 사용자 고유 id
     * @param request : PasswordChangeRequest.java
     * @return
     */
    @PutMapping("/pw/update")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        userService.updatePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 닉네임 변경
     * @param userId
     * @param request
     * @return
     */
    @PutMapping("/nickname/update")
    public ResponseEntity<Void> updateNickname(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody NicknameUpdateRequest request
    ) {
        userService.updateNickname(userId, request.getNickname());
        return ResponseEntity.ok().build();
    }
}
