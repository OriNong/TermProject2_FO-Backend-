package com.booklog.booklogbackend.util;

import com.booklog.booklogbackend.Model.vo.BookVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverBookSearchUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private static final String NAVER_BOOK_SEARCH_URL = "https://openapi.naver.com/v1/search/book.json";

    public List<BookVO> searchBooks(String query, String sort, int page, int limit) {
        try {
            // 네이버 API 파라미터 변환
            int start = (page - 1) * limit + 1;

            URI uri = new URI(NAVER_BOOK_SEARCH_URL + "?" +
                    "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&sort=" + mapSort(sort) +
                    "&start=" + start +
                    "&display=" + limit
            );

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseBooks(response.getBody());
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // 사용자가 선택한 정렬 방식에 따라 파라미터 정렬
    private String mapSort(String sort) {
        if ("pubdate".equals(sort)) {
            return "date"; // 네이버 API에서 출판일은 "date"
        }
        return "sim"; // 네이버 API에서 정확도는 "sim"
    }

    private List<BookVO> parseBooks(String jsonBody) throws Exception {
        JsonNode root = objectMapper.readTree(jsonBody);
        JsonNode items = root.path("items");

        List<BookVO> bookList = new ArrayList<>();
        for (JsonNode item : items) {
            BookVO book = BookVO.builder()
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
            bookList.add(book);
        }
        return bookList;
    }

    private Long parsePrice(String discount) {
        try {
            return Long.parseLong(discount);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
