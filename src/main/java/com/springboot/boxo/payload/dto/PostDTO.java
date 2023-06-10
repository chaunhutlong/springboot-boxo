package com.springboot.boxo.payload.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PostDTO extends IDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO author;
}
