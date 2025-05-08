package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import com.booklog.booklogbackend.Model.request.BookReviewRequest;
import com.booklog.booklogbackend.Model.vo.BookReviewVO;
import com.booklog.booklogbackend.mapper.BookReviewMapper;
import com.booklog.booklogbackend.mapper.BookcaseMapper;
import com.booklog.booklogbackend.service.BookReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {
    private final BookReviewMapper bookReviewMapper;
    private final BookcaseMapper bookcaseMapper;

    @Transactional
    @Override
    public void createReview(Long userId, BookReviewRequest bookReviewRequest) {

        // 리뷰를 작성하려는 도서가 '독서 완료(COMPLETED)'인지 조회
            // true : 독서 완료
            // false : 독서 완료되지 않음
        boolean isReadingCompleted = bookcaseMapper.isBookReadCompleted(userId, bookReviewRequest.getBookId(), BookReadingStatus.COMPLETED.name());
        if (!isReadingCompleted) {
            throw new IllegalStateException("해당 도서는 독서 완료 상태가 아닙니다.");
        }

        // 사용자가 해당 도서에 리뷰를 작성한 이력이 있는지 조회
            // true : 리뷰 작성 내역 존재
            // false : 리뷰 신규 작성 가능
        boolean reviewAlreadyExists = bookReviewMapper.isReviewExist(userId, bookReviewRequest.getBookId());
        if (reviewAlreadyExists) {
            throw new IllegalStateException("이미 이 도서에 대해 리뷰를 작성하셨습니다.");
        }

        // 도서에 새로운 리뷰 등록
        BookReviewVO review = BookReviewVO.builder()
                .userId(userId)
                .bookId(bookReviewRequest.getBookId())
                .reviewTitle(bookReviewRequest.getReviewTitle())
                .reviewContent(bookReviewRequest.getReviewContent())
                .rating(bookReviewRequest.getRating())
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        bookReviewMapper.insertReview(review);
    }
}
