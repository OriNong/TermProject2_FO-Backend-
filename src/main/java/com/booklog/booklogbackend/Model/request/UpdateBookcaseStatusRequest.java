package com.booklog.booklogbackend.Model.request;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookcaseStatusRequest {
    @NotNull(message = "서재 등록 정보는 필수입니다.")
    private Long bookcaseId;
}
