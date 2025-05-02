package com.booklog.booklogbackend.Model;

import com.booklog.booklogbackend.Model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

   private final UserVO user;

   public CustomUserDetails(UserVO user) {
       this.user = user;
   }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 권한 사용하지 않을 경우 null 또는 Collections.emptyList()
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 로그인 시 사용되는 식별자
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return user.getIsVerified() == com.booklog.booklogbackend.Model.VerificationStatus.VERIFIED;
    }
}
