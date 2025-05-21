package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import com.booklog.booklogbackend.Model.request.BookRegisterRequest;
import com.booklog.booklogbackend.Model.response.BookcaseResponse;
import com.booklog.booklogbackend.Model.response.ReviewIdByBookIdResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.Model.vo.BookcaseVO;
import com.booklog.booklogbackend.Model.vo.BookcaseWithBookVO;
import com.booklog.booklogbackend.exception.NotFoundException;
import com.booklog.booklogbackend.mapper.BookMapper;
import com.booklog.booklogbackend.mapper.BookReviewMapper;
import com.booklog.booklogbackend.mapper.BookcaseMapper;
import com.booklog.booklogbackend.service.BookcaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookcaseServiceImpl implements BookcaseService {

    private final BookcaseMapper bookcaseMapper;
    private final BookMapper bookMapper;
    private final BookReviewMapper bookReviewMapper;

    /**
     * 사용자가 '읽을 목록에 추가' 선택 시 사용자 서재에 도서 신규 등록
     * @param userId : 로그인 사용자 정보
     * @param request : 선택된 도서 정보
     */
    @Override
    @Transactional
    public void registerToBookcase(Long userId, BookRegisterRequest request) {
        // 1. 책이 이미 DB에 있는지 확인
        BookVO existingBook = bookMapper.findByIsbn(request.getIsbn());

        Long bookId;
        if (existingBook == null) {
            // 2. 없으면 insert 후 bookId 획득
            BookVO newBook = BookVO.builder()
                    .isbn(request.getIsbn())
                    .bookTitle(request.getBookTitle())
                    .bookLink(request.getBookLink())
                    .bookImg(request.getBookImg())
                    .bookAuthor(request.getBookAuthor())
                    .bookDiscount(request.getBookDiscount())
                    .bookPublisher(request.getBookPublisher())
                    .bookPubDate(request.getBookPubDate())
                    .bookDescription(request.getBookDescription())
                    .build();
            bookMapper.insertBook(newBook);
            bookId = newBook.getBookId(); // useGeneratedKeys로 반환됨
        } else {
            bookId = existingBook.getBookId();
            log.info("DB에서 조회된 bookID: {}",bookId.toString());
        }

        // 3. bookcase에 TO_READ로 등록
        BookcaseVO bookcase = BookcaseVO.builder()
                .userId(userId)
                .bookId(bookId)
                .readingStatus(BookReadingStatus.TO_READ)
                .build();

        if (bookcaseMapper.selectBookcaseStatus(userId, bookId) == null) {
            bookcaseMapper.insertBookcase(bookcase);
        }
        // 이미 등록돼 있으면 아무것도 안 함
    }

    /**
     * 서재에서 사용자가 '독서 시작' 선택 시 서재에 등록된 도서의 읽기 상태를 'READING'으로 변경
     * @param userId : 로그인 사용자 id
     * @param bookId : 선택한 도서 id
     */
    @Override
    @Transactional
    public void startReading(Long userId, Long bookId) {
        BookcaseVO existing = bookcaseMapper.selectBookcaseStatus(userId, bookId);

        if (existing == null) {
            // 등록이 안 되어 있으면 새로 TO_READ → READING 등록
            BookcaseVO newCase = BookcaseVO.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .readingStatus(BookReadingStatus.READING)
                    .build();
            bookcaseMapper.insertBookcase(newCase);
        } else {
            // 상태 업데이트
            BookcaseVO update = BookcaseVO.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .readingStatus(BookReadingStatus.READING)
                    .build();
            bookcaseMapper.updateBookcaseStatus(update);
        }
    }

    /**
     * 서재에서 사용자가 '독서 완료' 선택 시 해당 도서의 읽기 상태를 'COMPLETED'로 변경
     * @param userId : 로그인 사용자 ID
     * @param bookId : 선택 도서 DB 고유 ID
     */
    @Override
    @Transactional
    public void finishReading(Long userId, Long bookId) {
        BookcaseVO existing = bookcaseMapper.selectBookcaseStatus(userId, bookId);

        if (existing == null) {
            throw new NotFoundException("서재에 등록되지 않은 도서입니다.");
        }

        BookcaseVO update = BookcaseVO.builder()
                .userId(userId)
                .bookId(bookId)
                .readingStatus(BookReadingStatus.COMPLETED)
                .build(); //
        bookcaseMapper.updateBookcaseStatus(update);
    }

    /**
     * DB에서 사용자의 등록 도서 정보를 서재 고유 id로 조회하여 현재 읽기 상태 반환
     * 읽기 상태를 기준으로 이전 읽기 상태 스텝으로 변경 처리
     */
    @Override
    public void updateReadingStatus(Long userId, Long bookcaseId) {
        BookcaseVO current = bookcaseMapper.selectBookcaseById(bookcaseId);
        if (current == null || !current.getUserId().equals(userId)) {
            throw new NotFoundException("서재 정보를 찾을 수 없거나 권한이 없습니다.");
        }

        BookReadingStatus currentStatus = current.getReadingStatus();
        BookReadingStatus newStatus = switch (currentStatus) {
            case COMPLETED -> BookReadingStatus.READING;
            case READING -> BookReadingStatus.TO_READ;
            case TO_READ -> throw new IllegalStateException("이미 '읽을 목록' 상태입니다. 더 이상 되돌릴 수 없습니다.");
            default -> throw new IllegalArgumentException("알 수 없는 상태입니다: " + currentStatus);
        };

        bookcaseMapper.updateReadingStatusWithDates(bookcaseId, newStatus.name());
    }

    @Override
    public void deleteBookcase(Long userId, Long bookcaseId) {
        BookcaseVO current = bookcaseMapper.selectBookcaseById(bookcaseId);
        if (current == null || !current.getUserId().equals(userId)) {
            throw new NotFoundException("서재 정보를 찾을 수 없거나 권한이 없습니다.");
        }
        bookcaseMapper.deleteBookcaseById(bookcaseId);
    }

    /**
     * 로그인 사용자가 서재 페이지 접속 시 해당 사용자의 서재 전체 정보 로드
     * @param userId : 로그인 사용자 id
     * @return : BookcaseResponse -> 프론트엔드 렌더링 처리를 최적화 한 Response 클래스
     */
    public List<BookcaseResponse> getBookcaseByUser(Long userId) {
        List<BookcaseWithBookVO> list = bookcaseMapper.selectBookcaseByUserId(userId);

        return list.stream().map(joined -> {
            String statusText = null;

            if (joined.getReadingStatus() == BookReadingStatus.READING) {
                LocalDate start = joined.getReadingStartedAt().toLocalDate();
                long days = Duration.between(start.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
                statusText = (days == 0 ? 1 : days + 1) + "일째 독서 중입니다.";
            } else if (joined.getReadingStatus() == BookReadingStatus.COMPLETED) {
                LocalDate start = joined.getReadingStartedAt().toLocalDate();
                LocalDate end = joined.getReadingFinishedAt().toLocalDate();
                long days = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
                statusText = "총 " + (days == 0 ? 1 : days) + "일 동안 읽었습니다.";
            } else {
                statusText = "독서를 시작해보세요";
            }

            return BookcaseResponse.builder()
                    .bookcaseId(joined.getBookcaseId()) // 읽기 상태 되돌리기를 위해 id 반환 추가
                    .bookId(joined.getBookId()) // isbn 추가
                    .bookTitle(joined.getBookTitle())
                    .bookImg(joined.getBookImg())
                    .bookAuthor(joined.getBookAuthor())
                    .readingStatus(joined.getReadingStatus())
                    .statusText(statusText)
                    .build();
        }).toList();
    }

    /**
     * 도서 id와 사용자 id로 reviewId 조회하여 반환
     * @param userId
     * @param bookId
     * @return
     */
    @Override
    public ReviewIdByBookIdResponse getReviewByUserAndBook(Long userId, Long bookId) {
        boolean isReviewExist = bookReviewMapper.isReviewExist(userId, bookId);
        if (isReviewExist) {
            ReviewIdByBookIdResponse response = bookReviewMapper.getReviewIdByBookAndUserId(userId, bookId);
            response.setExists("EXIST");
            return response;
        } else {
            return ReviewIdByBookIdResponse.builder()
                    .reviewId(null)
                    .bookId(bookId)
                    .exists("NOT_EXIST")
                    .build();
        }
    }

    @Override
    public BookcaseVO getBookcaseStatus(Long userId, Long bookId) {
        return bookcaseMapper.selectBookcaseStatus(userId, bookId);
    }
}