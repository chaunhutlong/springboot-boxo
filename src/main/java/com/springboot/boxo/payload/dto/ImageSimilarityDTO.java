package com.springboot.boxo.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageSimilarityDTO {
    private Long book_id;
    private Double score;
    private String book_image_url;
    private String embedding_id;
}
