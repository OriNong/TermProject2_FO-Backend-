package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import com.booklog.booklogbackend.Model.response.BookSearchResponse;
import com.booklog.booklogbackend.Model.response.BookWithReviewStaticsResponse;
import com.booklog.booklogbackend.Model.response.BookcaseStatsResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.mapper.BookMapper;
import com.booklog.booklogbackend.service.BookService;
import com.booklog.booklogbackend.util.NaverBookSearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final NaverBookSearchUtil naverBookSearchUtil;
    private final BookMapper bookMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper; // JSON <-> 객체 변환용

    private static final long CACHE_TTL = 7L; // 7일 캐시

    /**
     * 검색창에서 도서명/저자로 검색 시 검색 결과 반환
     * 1. Redis 캐시 조회 2. 캐시 조회 실패 시 네이버 도서 검색 Api 호출
     * @param query : 검색 키워드
     * @param sort : 정렬 기준 (선택, 기본값 "accuracy")
     * @param page : 페이지 번호 (선택, 기본값 1)
     * @param limit : 페이지당 결과 수 (선택, 기본값 10)
     * @return : BookVO 리스트
     */
    @Override
    public BookSearchResponse searchBooks(String query, String sort, int page, int limit) {
        String cacheKey = buildCacheKey(query, sort, page, limit);

        // 1. Redis 캐시 먼저 조회
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            log.debug("Redis에서 도서 정보 조회");
            try {
                return objectMapper.readValue(cachedValue,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, BookVO.class));
            } catch (Exception e) {
                // 캐시 데이터 파싱 실패시 무시하고 새로 가져옴
                log.error("Redis 조회 실패");
            }
        }

        // 2. 네이버 API 호출
        BookSearchResponse books = naverBookSearchUtil.searchBooksWithMeta(query, sort, page, limit);
        log.debug("Redis에 도서 정보 없음");
        log.debug("네이버 도서 검색 api 호출");
        try {
            // 3. 결과를 Redis에 저장
            String jsonData = objectMapper.writeValueAsString(books);
            redisTemplate.opsForValue().set(cacheKey, jsonData, CACHE_TTL, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("Redis 저장 실패");
        }

        return books;
    }

    /**
     * 도서 상세페이지에서 해당 도서의 전체 서재 통계 정보 조회
     * @return : /서재에 등록한 사용자 수 총합/  /현재 독서 중인 사용자 수/  /독서 완료한 사용자 수/
     */
    @Override
    public BookcaseStatsResponse getTotalBookcaseStats(Long bookId) {
        int total = bookMapper.countAllBookcaseStatsByBookId(bookId);
        int reading = bookMapper.countBookcaseStatsByBookIdAndStatus(bookId, BookReadingStatus.READING.name());
        int completed = bookMapper.countBookcaseStatsByBookIdAndStatus(bookId, BookReadingStatus.COMPLETED.name());
        return new BookcaseStatsResponse(total, reading, completed);
    }

    /**
     * 도서 상세페이지 버튼 조건부 표시를 위해 서재에서 readingStatus 조회
     * @return : readingStatus를 String으로 반환
     */
    @Override
    public String getReadingStatus(Long userId, Long bookId) {
        return bookMapper.selectBookcaseReadingStatus(userId, bookId);
    }

    /**
     * 도서 상세 보기 클릭 시 isbn으로 해당 도서 조회
     * 1. DB 조회 2. Redis 캐시 조회 3. 네이버 검색 api 호출
     * @param isbn : 선택된 도서의 isbn 값
     * @return : 도서 단건 조회 결과 반환
     */
    @Override
    public BookVO getBookByIsbn(String isbn) {

        // 1. DB에 이미 등록된 도서인지 확인
        BookVO book = bookMapper.findByIsbn(isbn);
        if (book != null) {
            return book;
        }

        String cacheKey = buildCacheKey(isbn);
        // 2. Redis 캐시 조회
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, BookVO.class);
            }
        } catch (Exception e) {
            log.warn("Redis 캐시 파싱 실패 - isbn: {}", isbn, e);
        }

        // 3. 네이버 API 호출
        BookVO naverBook = naverBookSearchUtil.searchBookByIsbn(isbn);

        // 4. 캐시 저장
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(naverBook), CACHE_TTL, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis 캐시 저장 실패 - isbn: {}", isbn, e);
        }

        return naverBook;
    }

    /**
     * 리뷰가 1건 이상 등록되어 있는 도서 조회 (메인페이지 진입 시)
     * @return : 도서 정보 + 해당 도서의 리뷰 통계 정보(평균 평점, 등록 리뷰 수)
     */
    @Override
    public List<BookWithReviewStaticsResponse> getBooksWithReviewSummary() {
        return bookMapper.selectBooksWithReviewStatics();
    }

    /**
     * 키워드 기반 도서 검색 결과 조회 시 Redis 저장 캐시키 형식
     */
    private String buildCacheKey(String query, String sort, int page, int limit) {
        return String.format("book:search:%s:%s:%d:%d", query, sort, page, limit);
    }

    /**
     * isbn 으로 도서 단건 조회 시 Redis 저장 캐시키 형식
     */
    private String buildCacheKey(String isbn) {
        return String.format("book:isbn:%s", isbn);
    }
}
