package com.springboot.boxo.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PythonReviewDTO {
    private Long book_id;
    private Long user_id;
    private int rating;
}
