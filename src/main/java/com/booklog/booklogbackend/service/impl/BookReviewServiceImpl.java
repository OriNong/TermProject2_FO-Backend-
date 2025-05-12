package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import com.booklog.booklogbackend.Model.request.BookReviewRequest;
import com.booklog.booklogbackend.Model.request.ReviewCommentRequest;
import com.booklog.booklogbackend.Model.response.*;
import com.booklog.booklogbackend.Model.vo.BookReviewVO;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.mapper.BookMapper;
import com.booklog.booklogbackend.mapper.BookReviewMapper;
import com.booklog.booklogbackend.mapper.BookcaseMapper;
import com.booklog.booklogbackend.service.BookReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {
    private final BookReviewMapper bookReviewMapper;
    private final BookcaseMapper bookcaseMapper;
    private final BookMapper bookMapper;

    /**
     * 리뷰 작성 페이지 진입 시 도서 정보 표시
     */
    @Override
    public BookForNewReviewResponse getReviewRequestBook(Long bookId) {
        BookVO book = bookMapper.getBookByBookId(bookId);

        if (book == null) {
            throw new NoSuchElementException("도서 없음: " + bookId);
        }

        return BookForNewReviewResponse.builder()
                .bookId(book.getBookId())
                .bookTitle(book.getBookTitle())
                .bookAuthor(book.getBookAuthor())
                .bookImg(book.getBookImg())
                .bookPublisher(book.getBookPublisher())
                .build();
    }

    /**
     * 신규 도서 리뷰 작성
     */
    @Transactional
    @Override
    public void registerReview(Long userId, BookReviewRequest bookReviewRequest) {

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

    /**
     * 특정 도서에 등록된 리뷰 목록 조회
     */
    @Override
    public List<BookReviewResponse> getReviewsByBookId(Long bookId, Long userId) {
        return bookReviewMapper.selectReviewByBookId(bookId, userId);
    }

    /**
     * 특정 리뷰 상세 조회
     */
    @Override
    public BookReviewDetailResponse getReviewDetail(Long reviewId, Long userId) {
        return bookReviewMapper.selectReviewDetailById(reviewId, userId);
    }

    /**
     * 사용자가 작성한 리뷰 목록 조회
     */
    @Override
    public List<MyReviewResponse> getMyReviews(Long userId) {
        return bookReviewMapper.selectMyReviews(userId);
    }
}
