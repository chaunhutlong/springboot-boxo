package com.springboot.boxo.service;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.NotificationDTO;
import com.springboot.boxo.payload.request.NotificationRequest;

public interface NotificationService {
    void createNotification(NotificationRequest notificationRequest);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    PaginationResponse<NotificationDTO> getNotifications(Long userId, int page, int size);
}
