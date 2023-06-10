package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Post;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.PostDTO;
import com.springboot.boxo.repository.PostRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.PostService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private static final String POST_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Post with id {0} not found";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public PaginationResponse<PostDTO> getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        try {
            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = "title";
            }

            Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
            Page<Post> posts = postRepository.findAll(pageable);
            List<PostDTO> content = posts.getContent().stream().map(this::mapToDTO).toList();

            return PaginationUtils.createPaginationResponse(content, posts);
        } catch (Exception e) {
            HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
    }

    @Override
    public PostDTO getPostById(Long id) {
        var post = postRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(POST_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        return mapToDTO(post);
    }

    @Override
    public PostDTO createPost(Long userId, PostDTO postDTO) {
        try {
            var post = mapToEntity(postDTO);
            var user = userRepository.findById(userId).orElseThrow();
            post.setAuthor(user);
            post = postRepository.save(post);
            return mapToDTO(post);
        } catch (Exception e) {
            HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
    }

    @Override
    public PostDTO updatePost(Long id, PostDTO postDTO) {
        try {
            var post = postRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(POST_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));

            mapToEntity(postDTO, post);

            post = postRepository.save(post);
            return mapToDTO(post);
        } catch (Exception e) {
            HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
    }

    @Override
    public void deletePost(Long id) {
        try {
            var post = postRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(POST_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
            postRepository.delete(post);
        } catch (Exception e) {
            HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
    }

    private PostDTO mapToDTO(Post post) {
        return modelMapper.map(post, PostDTO.class);
    }

    private Post mapToEntity(PostDTO postDTO) {
        return modelMapper.map(postDTO, Post.class);
    }

    private void mapToEntity(PostDTO postDTO, Post post) {
        modelMapper.map(postDTO, post);
    }
}

