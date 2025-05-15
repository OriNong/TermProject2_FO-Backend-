package com.booklog.booklogbackend.Model;

import com.booklog.booklogbackend.Model.vo.UserVO;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    @Getter
    private final Long userId;
    private final String email;
    private final String password;
    @Getter
    private final String nickname;
    @Getter
    private final VerificationStatus verificationStatus;

    public CustomUserDetails(UserVO user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.verificationStatus = user.getIsVerified();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 체크를 하지 않으므로 최소한의 기본 권한만 반환
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // 이메일 인증 여부 확인 헬퍼 메소드
    public boolean isEmailVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }
}