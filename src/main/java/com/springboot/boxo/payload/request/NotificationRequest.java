package com.springboot.boxo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private Long userId;
    private long orderId;
    private String title;
    private String content;
    private String type;
}
