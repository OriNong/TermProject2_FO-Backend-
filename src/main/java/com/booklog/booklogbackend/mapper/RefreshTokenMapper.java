package com.booklog.booklogbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface RefreshTokenMapper {
    void save(@Param("userId")Long userId, @Param("token")String token, @Param("expiryDate") java.util.Date expiryDate);
    String findByuserId(@Param("userId") Long userId);
    void deleteByUserId(@Param("userId") Long userId);
}
