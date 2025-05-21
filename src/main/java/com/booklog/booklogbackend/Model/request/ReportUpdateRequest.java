package com.booklog.booklogbackend.Model.request;

import com.booklog.booklogbackend.Model.ReviewReportReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportUpdateRequest {

    @NotNull(message = "신고 사유 코드는 필수입니다.")
    private ReviewReportReason reasonCode;  // Enum 타입

    @NotBlank(message = "신고 상세 내용은 비워둘 수 없습니다.")
    @Size(max = 100, message = "기타 사유는 100자 이하로 입력해주세요.")
    private String reason;  // 상세 설명 (기타 또는 보충 설명)

}
