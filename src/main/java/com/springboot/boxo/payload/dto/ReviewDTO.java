package com.springboot.boxo.payload.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReviewDTO extends IDTO {
    private Long id;
    private String content;
    private int rating;
    private Long bookId;
    private UserDTO user;
}
