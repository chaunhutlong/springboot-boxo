package com.springboot.boxo.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> {
    private List<T> datas;
    private int page;
    private int limit;
    private long totalResults;
    private int totalPages;
    private boolean last;
}