package com.springboot.boxo.payload.request;

import com.springboot.boxo.payload.dto.PythonBookDTO;
import com.springboot.boxo.payload.dto.PythonReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PythonEmbeddingBooksRequest {
    private List<PythonBookDTO> books;
    private List<PythonReviewDTO> reviews;
}
