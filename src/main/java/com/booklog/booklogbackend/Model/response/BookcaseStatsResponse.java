package com.booklog.booklogbackend.Model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookcaseStatsResponse {
    private int total;      // 전체 등록
    private int reading;    // 읽는 중
    private int completed;  // 독서 완료
}