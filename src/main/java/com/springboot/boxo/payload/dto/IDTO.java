package com.springboot.boxo.payload.dto;

import lombok.Data;

@Data
public abstract class IDTO {
    private String createDate;
    private String updateDate;
    private String createBy;
    private String updateBy;
}