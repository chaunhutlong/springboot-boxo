package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Page<Publisher> findAll(Pageable pageable);
    Publisher findByName(String name);
    @Query("SELECT p FROM Publisher p WHERE p.name LIKE %:name%")
    List<Publisher> findByNameContaining(String name);
}
