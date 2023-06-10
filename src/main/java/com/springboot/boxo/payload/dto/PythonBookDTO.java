package com.springboot.boxo.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PythonBookDTO {
    private Long book_id;
    private String title;
    private List<Long> genre_ids;
}
