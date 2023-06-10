package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
