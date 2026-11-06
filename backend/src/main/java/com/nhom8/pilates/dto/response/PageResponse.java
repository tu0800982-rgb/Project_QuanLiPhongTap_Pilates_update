package com.nhom8.pilates.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageResponse(Page<T> source) {
        this.content = source.getContent();
        this.page = source.getNumber();
        this.size = source.getSize();
        this.totalElements = source.getTotalElements();
        this.totalPages = source.getTotalPages();
    }
    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}
