package com.springboot.boxo.service;

import com.springboot.boxo.payload.UserDto;

public interface UserService {
    UserDto findByIdentity(String identity);
    UserDto findById(Long id);
    UserDto findByUsername(String username);
    UserDto findByEmail(String email);
    UserDto findByUsernameOrEmail(String email, String username);
}
