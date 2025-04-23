package com.booklog.booklogbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface RefreshTokenMapper {
    void save(Long userId, String tokenId, String token, java.util.Date expiryDate);
    String findTokenByTokenId(String tokenId);
    void deleteByUserId(Long userId);
}
