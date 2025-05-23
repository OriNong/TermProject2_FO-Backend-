package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import com.booklog.booklogbackend.Model.request.BookReviewCreateRequest;
import com.booklog.booklogbackend.Model.request.BookReviewUpdateRequest;
import com.booklog.booklogbackend.Model.response.*;
import com.booklog.booklogbackend.Model.vo.BookReviewVO;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.exception.AlreadyExistException;
import com.booklog.booklogbackend.exception.BadRequestException;
import com.booklog.booklogbackend.exception.NotFoundException;
import com.booklog.booklogbackend.mapper.BookMapper;
import com.booklog.booklogbackend.mapper.BookReviewMapper;
import com.booklog.booklogbackend.mapper.BookcaseMapper;
import com.booklog.booklogbackend.service.BookReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
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
            throw new NotFoundException("도서 없음: " + bookId);
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
     * 신규 도서 리뷰 작성 -> 실패한 로직
     */
//    @Transactional
//    @Override
//    public void registerReview(Long userId, BookReviewCreateRequest bookReviewCreateRequest) {
//
//        // 리뷰를 작성하려는 도서가 '독서 완료(COMPLETED)'인지 조회
//            // true : 독서 완료
//            // false : 독서 완료되지 않음
//        boolean isReadingCompleted = bookcaseMapper.isBookReadCompleted(userId, bookReviewCreateRequest.getBookId(), BookReadingStatus.COMPLETED.name());
//        if (!isReadingCompleted) {
//            throw new BadRequestException("해당 도서는 독서 완료 상태가 아닙니다.");
//        }
//        log.info(bookReviewCreateRequest.toString());
//        log.info(userId.toString());
//        ReviewIdByBookIdResponse response = bookReviewMapper.getReviewIdByBookAndUserId(userId, bookReviewCreateRequest.getBookId());
//
//        if (response.getIsDeleted()){
//            log.info("논리적 삭제된 리뷰 존재. 해당 리뷰 id : {}", response.getReviewId());
//            try {
//                // 먼저 기존 리뷰를 삭제한다. (외래키 제약조건에 의해 연관된 모든 자식 요소들이 삭제됌.)
//                log.debug("기존 리뷰 물리적 삭제 진행, 삭제 대상 리뷰 ID : " + response.getReviewId());
//                bookReviewMapper.reviewPhysicalDeletion(response.getReviewId());
//                // 도서에 새로운 리뷰 등록
//                log.debug("기존 soft deleted 리뷰 삭제 완료. 신규 리뷰 등록 진행");
//                BookReviewVO review = BookReviewVO.builder()
//                        .userId(userId)
//                        .bookId(bookReviewCreateRequest.getBookId())
//                        .reviewTitle(bookReviewCreateRequest.getReviewTitle())
//                        .reviewContent(bookReviewCreateRequest.getReviewContent())
//                        .rating(bookReviewCreateRequest.getRating())
//                        .likesCount(0)
//                        .createdAt(LocalDateTime.now())
//                        .updatedAt(LocalDateTime.now())
//                        .build();
//                bookReviewMapper.insertReview(review);
//            } catch (Exception e) {
//                log.error("논리적 삭제 도서 물리적 삭제 후 도서 등록 중 오류 발생: {}", e.getMessage());
//                throw new BadRequestException("작업이 정상적으로 처리되지 않았습니다. 다시 시도하세요");
//            }
//        } else {
//            // 사용자가 해당 도서에 리뷰를 작성한 이력이 있는지 조회
//            // true : 리뷰 작성 내역 존재
//            // false : 리뷰 신규 작성 가능
//            boolean reviewAlreadyExists = bookReviewMapper.isReviewExist(userId, bookReviewCreateRequest.getBookId());
//            if (reviewAlreadyExists) {
//                throw new AlreadyExistException("이미 이 도서에 대해 리뷰를 작성하셨습니다.");
//            }
//
//            // 도서에 새로운 리뷰 등록
//            BookReviewVO review = BookReviewVO.builder()
//                    .userId(userId)
//                    .bookId(bookReviewCreateRequest.getBookId())
//                    .reviewTitle(bookReviewCreateRequest.getReviewTitle())
//                    .reviewContent(bookReviewCreateRequest.getReviewContent())
//                    .rating(bookReviewCreateRequest.getRating())
//                    .likesCount(0)
//                    .createdAt(LocalDateTime.now())
//                    .updatedAt(LocalDateTime.now())
//                    .build();
//            bookReviewMapper.insertReview(review);
//        }
//    }
    /**
     * 신규 도서 리뷰 작성
     */
    @Transactional
    @Override
    public void registerReview(Long userId, BookReviewCreateRequest req) {
        // 1. 독서 완료 여부 확인
        boolean isReadingCompleted = bookcaseMapper.isBookReadCompleted(userId, req.getBookId(), BookReadingStatus.COMPLETED.name());
        if (!isReadingCompleted) {
            throw new BadRequestException("해당 도서는 독서 완료 상태가 아닙니다.");
        }

        // 2. 기존 리뷰 조회 (논리 삭제 포함)
        ReviewIdByBookIdResponse response = bookReviewMapper.getReviewIdByBookAndUserId(userId, req.getBookId());

        if (response == null) {
            // 2-1. 리뷰가 처음인 경우 신규 등록
            bookReviewMapper.insertReview(buildReview(userId, req));
            return;
        }

        if (response.getIsDeleted()) {
            // 2-2. 리뷰가 논리 삭제된 경우 물리 삭제 후 신규 등록
            try {
                log.debug("기존 리뷰 물리적 삭제: ID = {}", response.getReviewId());
                bookReviewMapper.reviewPhysicalDeletion(response.getReviewId());

                log.debug("신규 리뷰 등록 시작");
                bookReviewMapper.insertReview(buildReview(userId, req));
            } catch (Exception e) {
                log.error("리뷰 물리 삭제 및 신규 등록 중 오류 발생: {}", e.getMessage());
                throw new BadRequestException("작업 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
            return;
        }

        // 2-3. 이미 리뷰가 정상 등록된 경우
        throw new AlreadyExistException("이미 이 도서에 대해 리뷰를 작성하셨습니다.");
    }

    /**
     * 작성된 리뷰 수정
     */
    @Transactional
    public void updateReview(Long reviewId, Long userId, BookReviewUpdateRequest request) {
        BookReviewVO existing = bookReviewMapper.selectByReviewId(reviewId);
        if (existing == null) {
            throw new NotFoundException("해당 리뷰를 찾을 수 없습니다.");
        }

        if (!existing.getUserId().equals(userId)) {
            throw new BadRequestException("리뷰 수정 권한이 없습니다.");
        }

        existing.setReviewTitle(request.getReviewTitle());
        existing.setReviewContent(request.getReviewContent());
        existing.setRating(request.getRating());

        bookReviewMapper.updateReview(existing);
    }

    /**
     * 작성된 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        BookReviewVO existing = bookReviewMapper.selectByReviewId(reviewId);
        if (existing == null) {
            throw new NotFoundException("해당 리뷰를 찾을 수 없습니다.");
        }

        if (!existing.getUserId().equals(userId)) {
            // BadRequestException 커스텀
            throw new BadRequestException("리뷰 삭제 권한이 없습니다.");
        }
        bookReviewMapper.deleteReview(reviewId);
    }

    /**
     * 특정 도서에 등록된 리뷰 목록 조회
     */
    @Override
    public List<BookReviewResponse> getReviewsByBookId(Long bookId, Long userId) {
        try {
            return bookReviewMapper.selectReviewByBookId(bookId, userId);
        } catch ( Exception e ) {
            throw new NotFoundException("리뷰 목록 조회에 실패했습니다.");
        }

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

    /**
     * 사용자가 작성한 리뷰 중 관리자가 삭제한 리뷰 목록 조회
     */
    @Override
    public List<MyReviewDeletedByAdminResponse> getMyReviewsAdminDeleted(Long userId) {
        return bookReviewMapper.selectMyReviewDeletedByAdmin(userId);
    }

    /**
     * 리뷰 등록 유틸
     * @return
     */
    private BookReviewVO buildReview(Long userId, BookReviewCreateRequest req) {
        return BookReviewVO.builder()
                .userId(userId)
                .bookId(req.getBookId())
                .reviewTitle(req.getReviewTitle())
                .reviewContent(req.getReviewContent())
                .rating(req.getRating())
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
