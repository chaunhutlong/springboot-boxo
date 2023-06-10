package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Role;
import com.springboot.boxo.entity.User;
import com.springboot.boxo.enums.RoleName;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.dto.UserDTO;
import com.springboot.boxo.repository.RoleRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final RoleRepository roleRepository;
    Random random = new Random();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.roleRepository = roleRepository;
    }

    public UserDTO findByIdentity(String identity) {
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

    @Override
    public void fakeUsers(int quantity) {
        String[] names = {"John", "Jane", "Jack", "Jill", "James", "Jenny", "Jeff", "Judy", "Joe", "Jade",
                "Jasper", "Jasmine", "Jared", "Jade", "Jenna", "Jesse", "Jocelyn", "Jude", "Julia", "Julian",
                "Juliet", "Liam", "Lily", "Lucas", "Layla", "Leo", "Luna", "Logan", "Leah", "Luke", "Lilly",
                "Mason", "Mia", "Mateo", "Madison", "Muhammad", "Maya", "Michael", "Mila", "Noah", "Nora",
            "Benjamin", "Bella", "Elijah", "Avery", "Oliver", "Aubrey", "William", "Camila", "James", "Charlotte",
            "Alexander", "Sophia", "Lucas", "Emma", "Mason", "Olivia", "Ethan", "Amelia", "Jacob", "Mia", "Michael",
            "Harper", "Daniel", "Evelyn", "Henry", "Abigail", "Jackson", "Emily", "Sebastian", "Elizabeth", "Aiden",
            "Mila", "Matthew", "Ella", "Samuel", "Avery", "David", "Sofia", "Joseph", "Camila", "Carter", "Aria",
            "Owen", "Scarlett", "Wyatt", "Victoria", "John", "Madison", "Jack", "Luna", "Luke", "Grace", "Jayden",
            "Chloe", "Dylan", "Penelope", "Grayson", "Layla", "Levi", "Riley", "Isaac", "Zoey", "Gabriel", "Nora"};

        List<User> users = new ArrayList<>();
        // find the last user id and increment it by 1
        long userId = userRepository.findTopByOrderByIdDesc().getId() + 1;
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(String.valueOf(RoleName.ROLE_USER)).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));

        for (int i = 0; i < quantity; i++) {
            User user = new User();
            int randomNameIndex = random.nextInt(names.length);
            String username = names[randomNameIndex] + i;
            user.setId(userId++);
            user.setUsername(username);
            user.setRoles(roles);
            user.setName(names[randomNameIndex]);
            user.setEmail(username + "@gmail.com");
            user.setPassword("$2a$10$Z.FK5aACk2O5EoSnWozydOQbACZ1OLAQJs4RLfz1kYzwTDH9pTzKW");
            users.add(user);
        }

        userRepository.saveAll(users);
    }

    private UserDTO mapToDto(User user) {
        return mapper.map(user, UserDTO.class);
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return mapToDto(user);
    }

    public UserDTO findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(this::mapToDto).orElse(null);

    }

    public UserDTO findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(this::mapToDto).orElse(null);

    }

    public UserDTO findByUsernameOrEmail(String email, String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, email);

        return user.map(this::mapToDto).orElse(null);

    }
}