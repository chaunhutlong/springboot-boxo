package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Profile;
import com.springboot.boxo.entity.Role;
import com.springboot.boxo.entity.User;
import com.springboot.boxo.enums.RoleName;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.UserInfoResponse;
import com.springboot.boxo.payload.dto.UserDTO;
import com.springboot.boxo.payload.request.*;
import com.springboot.boxo.repository.ProfileRepository;
import com.springboot.boxo.repository.RoleRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.security.JwtTokenProvider;
import com.springboot.boxo.service.AuthService;
import com.springboot.boxo.service.EmailService;
import com.springboot.boxo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${fe.url}")
    private String frontEndUrl;
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           ProfileRepository profileRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           UserService userService, EmailService emailService, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AuthResponse loginWithIdentityAndPassword(LoginRequest loginRequest) {
        String identity = loginRequest.getIdentity();

        UserDTO user = userService.findByUsernameOrEmail(identity, identity);

        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect!.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                identity, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));
        authResponse.setUser(user);

        return authResponse;
    }

    @Override
    public AuthResponse register(@NotNull RegisterRequest registerRequest) {

        // add check for username exists in database
        if (Boolean.TRUE.equals(userRepository.existsByUsername(registerRequest.getUsername()))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        // add check for email exists in database
        if (Boolean.TRUE.equals(userRepository.existsByEmail(registerRequest.getEmail()))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        String password = registerRequest.getPassword();
        if (!isPasswordValid(password)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid password format.");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "User Role not set."));
        roles.add(userRole);
        user.setRoles(roles);

        Profile profile = new Profile();
        user.setProfile(profile);
        userRepository.save(user);

        return loginWithIdentityAndPassword(new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword()));
    }

    @Override
    public AuthResponse loginWithGoogle(HttpServletRequest request, LoginGoogleRequest loginGoogleRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginGoogleRequest.getAccessToken());

        ResponseEntity<UserInfoResponse> responseEntity = restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserInfoResponse.class
        );
        UserInfoResponse userInfoResponse = responseEntity.getBody();

        if (userInfoResponse == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get user info");
        }

        String email = userInfoResponse.getEmail();
        String name = userInfoResponse.getName();
        String picture = userInfoResponse.getPicture();

        var user = userRepository.findUserByEmail(email);

        if (user == null) {
            String username = extractUsernameFromEmail(email);
            user = userService.createUser(email, username, name);
            user.setPassword(passwordEncoder.encode("123456"));
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "User Role not set."));
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
            createProfile(picture);
        }

        UserDetails userDetails = buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(convertToDTO(user));
        authResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));

        return authResponse;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String email = forgotPasswordRequest.getEmail();
            User user = userRepository.findUserByEmail(email);
            if (user == null) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Email is not exists!.");
            }

            // Generate token
            String token = generateToken();

            // Set expired date 15 minutes
            LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(15);

            // Save token to database
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiredDate(expiredDate);

            // Send email
            String subject = "Reset password";
            // front end url
            String content = "Please click the link below to reset your password: \n"
                    + frontEndUrl + "/reset-password/" + "?token=" + token;
            emailService.sendEmail(email, subject, content);

            userRepository.save(user);

        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {
        try {
            String password = resetPasswordRequest.getPassword();

            User user = userRepository.findUserByResetPasswordToken(token);

            if (user == null) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Token is invalid!.");
            }

            LocalDateTime expiredDate = user.getResetPasswordTokenExpiredDate();

            if (LocalDateTime.now().isAfter(expiredDate)) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Token is expired!.");
            }

            user.setPassword(passwordEncoder.encode(password));
            user.setResetPasswordToken(null);

            userRepository.save(user);

        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private boolean isPasswordValid(String password) {
        // At least 8 characters, containing uppercase, lowercase, and numbers.
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(passwordRegex);
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void createProfile(String avatar) {
        Profile profile = new Profile();
        profile.setAvatar(avatar);
        profileRepository.save(profile);
    }

    private String extractUsernameFromEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        }
        return email;
    }

    private UserDetails buildUserDetails(User user) {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        Set<Role> roles = user.getRoles();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities
        );
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
