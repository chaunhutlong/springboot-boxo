package com.springboot.boxo.payload;

import com.springboot.boxo.payload.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UserDTO user;
    private String accessToken;
}
