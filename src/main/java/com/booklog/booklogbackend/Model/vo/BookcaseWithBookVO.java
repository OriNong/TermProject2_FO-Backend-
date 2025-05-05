package com.booklog.booklogbackend.Model.vo;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookcaseWithBookVO {
    private Long bookId;
    private String bookTitle;
    private String bookImg;
    private String bookAuthor;

    private BookReadingStatus readingStatus;
    private LocalDateTime readingStartedAt;
    private LocalDateTime readingFinishedAt;
}
