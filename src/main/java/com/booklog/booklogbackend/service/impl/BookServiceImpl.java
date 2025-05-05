package com.booklog.booklogbackend.service.impl;

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
    public List<BookVO> searchBooks(String query, String sort, int page, int limit) {
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
        List<BookVO> books = naverBookSearchUtil.searchBooks(query, sort, page, limit);
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
     * 서재에서 readingStatus 조회 (도서 상세페이지 버튼 조건부 표시)
     * @return : readingStatus를 String으로 반환
     */
    @Override
    public String getReadingStatus(Long userId, Long bookId) {
        return bookMapper.selectBookcaseReadingStatus(userId, bookId);
    }

    /**
     * 도서 상세 보기 클릭 시 isbn으로 해당 도서 조회
     * 1. Redis 캐시 조회 2. 네이버 검색 api 호출
     * @param isbn : 선택된 도서의 isbn 값
     * @return : 도서 단건 조회 결과 반환
     */
    //!! 추후 리뷰 작성된 도서 테이블 저장 시 해당 조회 로직을 1과 2 사이에 추가 !!
    @Override
    public BookVO getBookByIsbn(String isbn) {
        String cacheKey = buildCacheKey(isbn);

        // 1. Redis 캐시 조회
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, BookVO.class);
            } catch (Exception e) {
                log.warn("캐시 파싱 실패, isbn: {}", isbn);
            }
        }

        // 2. Naver API 조회
        BookVO book = naverBookSearchUtil.searchBookByIsbn(isbn);

        // 3. 캐시 저장
        if (book != null) {
            try {
                redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(book), CACHE_TTL, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("캐시 저장 실패, isbn: {}", isbn);
            }
        }

        return book;
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
