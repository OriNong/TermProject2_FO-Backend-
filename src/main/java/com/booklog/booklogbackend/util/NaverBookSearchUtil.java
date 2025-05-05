package com.booklog.booklogbackend.util;

import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.exception.NaverApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverBookSearchUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    // 키워드(도서명/저자)로 검색 시 네이버 도서 검색 api 기본 URL
    private static final String NAVER_BOOK_SEARCH_URL = "https://openapi.naver.com/v1/search/book.json";

    // ISBN으로 도서 검색 시 네이버 도서 검색 api 기본 URL
    private static final String NAVER_BOOK_DETAIL_URL = "https://openapi.naver.com/v1/search/book_adv.json";

    /**
     * 책 검색 메서드
     * @param query 검색어
     * @param sort 정렬 방식 (pubdate: 출판일, sim: 정확도)
     * @param page 페이지 번호
     * @param limit 한 페이지당 결과 수
     * @return 검색된 책 목록
     */
    public List<BookVO> searchBooks(String query, String sort, int page, int limit) {
        try {
            // 네이버 API 파라미터 변환
            int start = (page - 1) * limit + 1;

            // URI 생성
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = NAVER_BOOK_SEARCH_URL + "?query=" + encodedQuery +
                    "&sort=" + mapSort(sort) +
                    "&start=" + start +
                    "&display=" + limit;
            URI uri = new URI(url);

            // 요청 수행
            ResponseEntity<String> response = executeNaverApiRequest(uri);

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseBookList(response.getBody());
            } else {
                log.warn("네이버 책 검색 API 오류: {}", response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("네이버 책 검색 중 예외 발생: {}", e.getMessage(), e);
            throw new NaverApiException("책 검색 중 오류가 발생했습니다", e);
        }
    }

    /**
     * ISBN으로 책 상세 정보 조회
     * @param isbn 검색할 ISBN
     * @return 책 정보 또는 찾지 못한 경우 Optional.empty()
     */
    public BookVO searchBookByIsbn(String isbn) {
        try {
            // URI 생성
            String encodedIsbn = URLEncoder.encode(isbn, StandardCharsets.UTF_8);
            String url = NAVER_BOOK_DETAIL_URL + "?d_isbn=" + encodedIsbn + "&display=1&start=1";
            URI uri = new URI(url);

            // 요청 수행
            ResponseEntity<String> response = executeNaverApiRequest(uri);

            if (response.getStatusCode() == HttpStatus.OK) {
                BookVO book = parseBookDetail(response.getBody());
                if (book == null) {
                    throw new NaverApiException("ISBN으로 책을 찾을 수 없습니다: " + isbn, null);
                }
                return book;
            } else {
                log.warn("네이버 ISBN 단건 조회 API 오류: {}, ISBN: {}", response.getStatusCode(), isbn);
                throw new NaverApiException("네이버 API 응답 오류: " + response.getStatusCode(), null);
            }
        } catch (Exception e) {
            if (e instanceof NaverApiException) {
                throw (NaverApiException) e;
            }
            log.error("네이버 ISBN 단건 조회 중 예외 발생: {}, ISBN: {}", e.getMessage(), isbn, e);
            throw new NaverApiException("ISBN 조회 중 오류가 발생했습니다: " + isbn, e);
        }
    }

    /**
     * 네이버 API 요청 공통 메서드
     */
    private ResponseEntity<String> executeNaverApiRequest(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    /**
     * 사용자가 선택한 정렬 방식에 따라 네이버 API 정렬 파라미터 매핑
     */
    private String mapSort(String sort) {
        if ("pubdate".equals(sort)) {
            return "date"; // 네이버 API에서 출판일은 "date"
        }
        return "sim"; // 네이버 API에서 정확도는 "sim"
    }

    /**
     * JSON 응답에서 책 목록 파싱
     */
    private List<BookVO> parseBookList(String jsonBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(jsonBody);
        JsonNode items = root.path("items");

        List<BookVO> bookList = new ArrayList<>();
        for (JsonNode item : items) {
            bookList.add(parseBookItem(item));
        }
        return bookList;
    }

    /**
     * JSON 응답에서 단일 책 정보 파싱
     */
    private BookVO parseBookDetail(String jsonBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(jsonBody);
        JsonNode items = root.path("items");

        if (items.isArray() && items.size() > 0) {
            return parseBookItem(items.get(0));
        }

        return null;
    }

    /**
     * 책 아이템 JSON 노드 파싱
     */
    private BookVO parseBookItem(JsonNode item) {
        return BookVO.builder()
                .isbn(item.path("isbn").asText())
                .bookTitle(item.path("title").asText())
                .bookLink(item.path("link").asText())
                .bookImg(item.path("image").asText())
                .bookAuthor(item.path("author").asText())
                .bookDiscount(parsePrice(item.path("discount").asText()))
                .bookPublisher(item.path("publisher").asText())
                .bookPubDate(item.path("pubdate").asText())
                .bookDescription(item.path("description").asText())
                .build();
    }

    /**
     * 가격 문자열을 숫자로 파싱
     */
    private Long parsePrice(String discount) {
        try {
            return StringUtils.hasText(discount) ? Long.parseLong(discount) : 0L;
        } catch (NumberFormatException e) {
            log.debug("가격 파싱 실패: {}", discount);
            return 0L;
        }
    }
}
