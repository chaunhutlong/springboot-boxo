package com.springboot.boxo.utils;

import com.springboot.boxo.payload.PaginationResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@UtilityClass
public final class PaginationUtils {

    public static <T> PaginationResponse<T> createPaginationResponse(List<T> content, Page<?> page) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }

    public static <T> PaginationResponse<T> createPaginationResponse(List<T> content, long totalElements, int pageNumber, int pageSize) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
        response.setLast(pageNumber == response.getTotalPages() - 1);
        return response;
    }

    public static Pageable convertToPageable(int pageNumber, int pageSize, String sortBy, String sortDir) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }

}
