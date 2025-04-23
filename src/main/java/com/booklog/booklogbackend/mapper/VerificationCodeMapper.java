package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.VerificationCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VerificationCodeMapper {
    void save(VerificationCode verificationCode);
    VerificationCode findByEmail(String email);
    void updateVerificationStatus(String email);
}
