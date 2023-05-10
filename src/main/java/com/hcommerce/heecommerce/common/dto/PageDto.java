package com.hcommerce.heecommerce.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageDto<T> {

    private final int pageNumber;

    private final int pageSize;

    private final int totalCount;

    private final List<T> items;
}
