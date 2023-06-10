package com.springboot.boxo.payload.dto;


import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private long userId;
    private long orderId;
    private String title;
    private String content;
    private String type;
    private boolean isRead;
    private java.sql.Timestamp createdDate;
}
