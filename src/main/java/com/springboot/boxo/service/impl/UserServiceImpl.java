package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.User;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.UserDto;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public UserDto findByIdentity(String identity) {
        var user = userRepository.findByUsernameOrEmail(identity, identity);
        if (user.isEmpty()) {
            // if identity is not username or email, check if it is id
            try {
                Long id = Long.parseLong(identity);
                user = userRepository.findById(id);
            } catch (NumberFormatException e) {
                throw new ResourceNotFoundException("User", "identity", identity);
            }
    }

        return user.map(this::mapToDto).orElseThrow(() -> new ResourceNotFoundException("User", "identity", identity));
    }

    private UserDto mapToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return mapToDto(user);
    }

    public UserDto findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(this::mapToDto).orElse(null);

    }

    public UserDto findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(this::mapToDto).orElse(null);

    }

    public UserDto findByUsernameOrEmail(String email, String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, email);

        return user.map(this::mapToDto).orElse(null);

    }
}