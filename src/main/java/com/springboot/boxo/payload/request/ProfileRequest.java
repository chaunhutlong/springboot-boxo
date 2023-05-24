package com.springboot.boxo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    private String biography;
    private String phone;
    private List<Long> addresses;
    private MultipartFile avatar;
}
