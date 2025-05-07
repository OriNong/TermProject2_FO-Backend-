package com.booklog.booklogbackend.Model.response;

import com.booklog.booklogbackend.Model.vo.BookVO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSearchResponse {
    private List<BookVO> books;
    private int total;
    private int page;
    private int limit;
}
