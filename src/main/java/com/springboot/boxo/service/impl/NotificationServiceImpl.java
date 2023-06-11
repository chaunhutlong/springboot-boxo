package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Notification;
import com.springboot.boxo.enums.NotificationType;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.NotificationDTO;
import com.springboot.boxo.payload.request.NotificationRequest;
import com.springboot.boxo.repository.NotificationRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.NotificationService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createNotification(NotificationRequest notificationRequest) {
        var notification = new Notification();
        notification.setTitle(notificationRequest.getTitle());
        notification.setContent(notificationRequest.getContent());
        notification.setUser(userRepository.findById(notificationRequest.getUserId()).orElseThrow());
        notification.setRead(false);
        notification.setOrderId(notificationRequest.getOrderId());
        notification.setType(NotificationType.valueOf(notificationRequest.getType()));
        notification.setCreatedDate(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        var notifications = notificationRepository.findAllByUserIdAndReadFalse(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public PaginationResponse<NotificationDTO> getNotifications(Long userId, int pageNumber, int pageSize) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, "createdDate", "desc");
        Page<Notification> pageNotification = notificationRepository.findAllByUserId(userId, pageable);

        List<NotificationDTO> notificationDTOs = pageNotification.getContent().stream()
                .map(this::convertToDto)
                .toList();
        return PaginationUtils.createPaginationResponse(notificationDTOs, pageNotification);
    }

    private NotificationDTO convertToDto(Notification notification) {
        return modelMapper.map(notification, NotificationDTO.class);
    }
}
