package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.response.UserProfileResponse;
import com.booklog.booklogbackend.Model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(UserVO userVO);
    UserVO findByEmail(String email);
    UserProfileResponse findByUserId(Long userId);
    UserVO findByTokenId(String tokenId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
