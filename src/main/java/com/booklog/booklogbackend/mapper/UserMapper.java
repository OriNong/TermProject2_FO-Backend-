package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.response.UserProfileResponse;
import com.booklog.booklogbackend.Model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(UserVO userVO);
    UserVO findByEmail(String email);
    UserProfileResponse findByUserId(Long userId);
    UserVO findByTokenId(String tokenId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    String getPasswordById(@Param("userId") Long userId);

    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    void updateNickname(@Param("userId") Long userId, @Param("nickname") String nickname);

    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

}
