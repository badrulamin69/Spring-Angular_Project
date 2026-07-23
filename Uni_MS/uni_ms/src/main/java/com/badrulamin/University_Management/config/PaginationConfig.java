package com.badrulamin.University_Management.config;

public final class PaginationConfig {
    private PaginationConfig() {}

    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE = 0;

    public static int clampSize(int size) {
        return Math.min(Math.max(1, size), MAX_PAGE_SIZE);
    }

    public static int clampPage(int page) {
        return Math.max(0, page);
    }
}
