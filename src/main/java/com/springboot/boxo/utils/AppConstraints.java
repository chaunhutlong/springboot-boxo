package com.springboot.boxo.utils;

public class AppConstraints {
    private AppConstraints() {
        throw new IllegalStateException("Utility class");
    }
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public  static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
}
