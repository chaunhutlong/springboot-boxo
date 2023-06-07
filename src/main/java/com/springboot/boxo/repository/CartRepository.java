package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId ORDER BY c.id ASC")
    List<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.isChecked = :checked ORDER BY c.id ASC")
    List<Cart> findByUserIdAndChecked(Long userId, boolean checked);

    @Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.totalPrice = :totalPrice WHERE c.user.id = :userId AND c.book.id = :bookId")
    void updateTotalPrice(Long userId, Long bookId, double totalPrice);
}