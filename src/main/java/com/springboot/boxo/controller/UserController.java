package com.springboot.boxo.controller;

import com.springboot.boxo.payload.UserDto;
import com.springboot.boxo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }

        // Build Get User By Username Or Email REST API
        @GetMapping("{identity}")
        public ResponseEntity<UserDto> getUserByIdentity(@PathVariable String identity){
            return ResponseEntity.ok(userService.findByIdentity(identity));
        }

}
