package com.booklog.booklogbackend.Model.request;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookcaseStatusRequest {
    private Long bookcaseId;
}
