package com.springboot.boxo.service;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.PostDTO;

public interface PostService {
    PaginationResponse<PostDTO> getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir);
    PostDTO getPostById(Long id);
    PostDTO createPost(Long userId, PostDTO postDTO);
    PostDTO updatePost(Long id, PostDTO postDTO);
    void deletePost(Long id);
}
