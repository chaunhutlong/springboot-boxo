package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Role;
import com.springboot.boxo.enums.RoleName;
import com.springboot.boxo.entity.User;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.LoginDto;
import com.springboot.boxo.payload.RegisterDto;
import com.springboot.boxo.payload.UserDto;
import com.springboot.boxo.repository.RoleRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.security.JwtTokenProvider;
import com.springboot.boxo.service.AuthService;
import com.springboot.boxo.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public AuthResponse loginWithIdentityAndPassword(LoginDto loginDto) {
        String identity = loginDto.getIdentity();

        UserDto user = userService.findByUsernameOrEmail(identity, identity);

        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect!.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                identity, loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));
        authResponse.setUser(user);

        return authResponse;
    }

    @Override
    public AuthResponse register(@NotNull RegisterDto registerDto) {

        // add check for username exists in database
        if (Boolean.TRUE.equals(userRepository.existsByUsername(registerDto.getUsername()))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        // add check for email exists in database
        if (Boolean.TRUE.equals(userRepository.existsByEmail(registerDto.getEmail()))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "User Role not set."));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        // login after register
        return loginWithIdentityAndPassword(new LoginDto(registerDto.getUsername(), registerDto.getPassword()));
    }
}
