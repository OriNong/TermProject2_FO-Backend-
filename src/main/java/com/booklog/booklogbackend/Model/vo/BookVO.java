package com.booklog.booklogbackend.Model.vo;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookVO {
    private Long bookId;                // 도서 고유 id
    private String isbn;                // 도서 isbn
    private String bookTitle;           // 도서 이름
    private String bookLink;            // 네이버 도서 정보 페이지
    private String bookImg;             // 도서 이미지 URL
    private String bookAuthor;          // 도서 저자
    private Long bookDiscount;          // 도서 가격
    private String bookPublisher;       // 도서 출판사
    private String bookPubDate;         // 도서 출판일
    private String bookDescription;     // 네이버 책 도서 설명
}
