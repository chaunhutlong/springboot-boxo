package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.UserDTO;
import com.springboot.boxo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

        @GetMapping("{identity}")
        public ResponseEntity<UserDTO> getUserByIdentity(@PathVariable String identity){
            return ResponseEntity.ok(userService.findByIdentity(identity));
        }
        @PostMapping("seed_user")
        public ResponseEntity<UserDTO> seedUser(){
            userService.seedUsers();
            return ResponseEntity.noContent().build();
        }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PostMapping("/fake")
//    public ResponseEntity<Void> fakeUsers(@RequestParam int quantity){
//        userService.fakeUsers(quantity);
//        return ResponseEntity.ok().build();
//    }
}
