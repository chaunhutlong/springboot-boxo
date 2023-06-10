package com.springboot.boxo.controller;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.NotificationDTO;
import com.springboot.boxo.payload.request.NotificationRequest;
import com.springboot.boxo.service.NotificationService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.createNotification(notificationRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read")
    public ResponseEntity<Void> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<NotificationDTO>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize)
    {
        return ResponseEntity.ok(notificationService.getNotifications(userId, pageNumber, pageSize));
    }
}
