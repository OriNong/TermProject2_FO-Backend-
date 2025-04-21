package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper {
    void insertUser(Users user);
    Users findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
