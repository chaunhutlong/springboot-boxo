package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.user.id = :userId and n.isRead = false")
    List<Notification> findAllByUserIdAndReadFalse(Long userId);

    @Query("select n from Notification n where n.user.id = :userId")
    Page<Notification> findAllByUserId(Long userId, Pageable pageable);
}
