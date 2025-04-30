package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.vo.BookVO;
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
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper; // JSON <-> 객체 변환용

    private static final long CACHE_TTL = 1L; // 1시간 캐시

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
                log.info("네이버 도서 검색 API 호출");
            }
        }

        // 2. 네이버 API 호출
        List<BookVO> books = naverBookSearchUtil.searchBooks(query, sort, page, limit);
        log.debug("Redis에 도서 정보 없음");
        log.debug("네이버 도서 검색 api 호출");
        try {
            // 3. 결과를 Redis에 저장
            String jsonData = objectMapper.writeValueAsString(books);
            redisTemplate.opsForValue().set(cacheKey, jsonData, CACHE_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            // 저장 실패해도 무시
        }

        return books;
    }

    private String buildCacheKey(String query, String sort, int page, int limit) {
        return String.format("book:search:%s:%s:%d:%d", query, sort, page, limit);
    }
}
