package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.isPaid = :paid")
    Payment findByOrderIdAndPaid(Long orderId, boolean paid);
}
