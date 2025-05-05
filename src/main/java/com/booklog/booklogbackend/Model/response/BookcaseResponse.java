package com.booklog.booklogbackend.Model.response;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookcaseResponse {
    private Long bookId;
    private String bookTitle;
    private String bookImg;
    private String bookAuthor;
    private BookReadingStatus readingStatus;
    private String statusText; // "3일째 독서 중입니다." 또는 "총 4일 동안 읽었습니다." 등
}

