package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.UserDTO;


public interface UserService {
    UserDTO findByIdentity(String identity);
    UserDTO findById(Long id);
    UserDTO findByUsername(String username);
    UserDTO findByEmail(String email);
    UserDTO findByUsernameOrEmail(String email, String username);
}
