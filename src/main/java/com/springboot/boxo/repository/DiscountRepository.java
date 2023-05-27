package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d FROM Discount d WHERE d.code = :code AND d.isActive = :isActive")
    Discount findByCodeAndIsActive(String code, boolean isActive);
}