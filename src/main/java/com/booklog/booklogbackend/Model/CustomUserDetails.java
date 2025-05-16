package com.booklog.booklogbackend.Model;

import com.booklog.booklogbackend.Model.VerificationStatus;
import com.booklog.booklogbackend.Model.vo.UserVO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String nickname;
    private final VerificationStatus verificationStatus;
    private final boolean isActive; // 계정 활성 상태 필드 추가

    public CustomUserDetails(UserVO user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.verificationStatus = user.getIsVerified();
        this.isActive = user.isActive(); // 여기서 주입
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        return true; // 기본 활성 상태 유지
    }

    // 이메일 인증 여부로 로그인 가능 여부 결정
    @Override
    public boolean isAccountNonLocked() {
        return this.verificationStatus == VerificationStatus.VERIFIED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성 상태 (is_active) 체크
    @Override
    public boolean isEnabled() {
        return this.isActive; // 계정 활성 상태에 따라 인증 가능 여부 결정
    }

    public boolean isEmailVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }
}
