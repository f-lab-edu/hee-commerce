package com.hcommerce.heecommerce.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageDto<T> {

    private final int pageNumber;

    private final int pageSize;

    private final int totalCount;

    private final List<T> items;

    @Builder
    public PageDto(int pageNumber, int pageSize, int totalCount, List<T> items) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.items = items;
    }
}
