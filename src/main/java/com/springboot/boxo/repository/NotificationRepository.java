package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    List<Notification> findAllByUserIdAndReadFalse(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdDate DESC")
    Page<Notification> findAllByUserId(Long userId, Pageable pageable);
}
