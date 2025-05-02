package com.booklog.booklogbackend.service.auth;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserVO user = userMapper.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + email);
        }
        return new CustomUserDetails(user);
    }
}
