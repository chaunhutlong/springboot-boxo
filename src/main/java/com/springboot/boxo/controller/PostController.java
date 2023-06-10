package com.springboot.boxo.controller;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.PostDTO;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.PostService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<PostDTO>> getPosts(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        var posts = postService.getAllPosts(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long postId) {
        var post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PostDTO postDTO) {
        Long userId = userDetails.getUserId();
        var post = postService.createPost(userId, postDTO);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long postId, @RequestBody PostDTO postDTO) {
        var post = postService.updatePost(postId, postDTO);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
