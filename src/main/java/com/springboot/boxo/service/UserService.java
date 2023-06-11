package com.springboot.boxo.service;

import com.springboot.boxo.entity.User;
import com.springboot.boxo.payload.dto.UserDTO;


public interface UserService {
    UserDTO findByIdentity(String identity);
    UserDTO findById(Long id);
    User createUser(String email, String username, String name);
    UserDTO findByUsernameOrEmail(String email, String username);
//    void fakeUsers(int quantity);
}
