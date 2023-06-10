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
        response.setDatas(content);
        response.setLimit(page.getSize());
        response.setPage(page.getNumber());
        response.setTotalResults(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }


    public static Pageable convertToPageable(int pageNumber, int pageSize, String sortBy, String sortDir) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }

}
