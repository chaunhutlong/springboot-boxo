package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Role;
import com.springboot.boxo.entity.User;
import com.springboot.boxo.enums.RoleName;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.dto.UserDTO;
import com.springboot.boxo.repository.RoleRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final Random random;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.roleRepository = roleRepository;
        this.random = new Random();
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
    public User createUser(String email, String username, String name) {
        User user = mapToEntity(email, username, name);
        return userRepository.save(user);
    }

    private UserDTO mapToDto(User user) {
        return mapper.map(user, UserDTO.class);
    }

    private User mapToEntity(String email, String username, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setUsername(username);
        return user;
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return mapToDto(user);
    }

    public UserDTO findByUsernameOrEmail(String email, String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, email);

        return user.map(this::mapToDto).orElse(null);

    }

    public UserDTO seedUser() {
        User user = new User();
        String sharedPassword = "$2a$10$ICqQU5EmeVBw5r3gWj.80.ttnj2ZaUrpLi7sPboYxtc1OIJNT8o9q";
        // Generate random data for the user
        user.setEmail(generateRandomEmail());
        user.setName(generateRandomName());
        user.setUsername(generateRandomUsername());
        user.setPassword(sharedPassword);
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "User Role not set."));
        roles.add(userRole);
        user.setRoles(roles);

        // Save the user to the database
        User savedUser = userRepository.save(user);

        return mapToDto(savedUser);
    }

    @Override
    public void seedUsers() {

        for (int i = 0; i < 1000; i++) {
            UserDTO userDTO = new UserDTO();
            seedUser();
        }
    }
    private String generateRandomEmail() {
        String[] domains = { "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "aol.com", "example.com", "test.com", "domain.com", "mail.com", "myemail.com",
                "company.com", "school.edu", "website.net", "service.org", "shop.co", "business.io", "tech.ai", "blog.xyz", "info.me", "support.dev",
                "workplace.us", "network.tv", "platform.app", "studio.fm", "store.store", "game.games", "music.audio", "video.video", "photo.pics",
                "social.social", "market.market", "news.news", "travel.travel", "food.food", "health.health", "sports.sports", "book.books",
                "art.art", "science.science", "finance.finance", "fashion.fashion", "fitness.fitness", "home.home", "tech.tech", "design.design",
                "gaming.gaming", "movie.movies", "car.cars", "animal.animals", "nature.nature", "business.business", "education.education", "foodie.foodie",
                "healthcare.healthcare", "technology.technology", "traveler.traveler", "sportslover.sportslover", "musiclover.musiclover", "artlover.artlover",
                "gamer.gamer", "photographer.photographer" };
        String[] prefixes = { "john", "jane", "smith", "doe", "alex", "emma", "david", "olivia", "michael", "sophia", "samuel", "lily", "jacob", "ava", "liam",
                "mia", "ethan", "noah", "charlotte", "benjamin", "grace", "henry", "lucy", "oliver", "amelia", "james", "ella", "william", "harper",
                "jackson", "scarlett", "andrew", "zoey", "matthew", "avery", "daniel", "hannah", "luke", "elizabeth", "christopher", "natalie", "owen",
                "claire", "nathan", "addison", "gabriel", "aria", "sebastian", "zoey", "julian", "claire", "ryan", "abigail", "joseph", "chloe",
                "jonathan", "madison", "isaiah", "ella", "carter", "audrey", "evan", "skylar", "christian", "piper", "nicholas", "riley" };
        String[] suffixes = { "123", "456", "789", "abc", "xyz", "00", "11", "22", "33", "44", "55", "66", "77", "88", "99", "qwerty", "test", "user", "random",
                "alpha", "beta", "omega", "gamer", "expert", "master", "ninja", "warrior", "king", "queen", "star", "wizard", "genius", "champion",
                "hero", "legend", "hunter", "pro", "savage", "beast", "guru", "viper", "sprinter", "phantom", "pioneer", "tiger", "wolf", "eagle",
                "hawk", "lion", "panther", "ace", "cyber", "tech", "x", "fire", "ice", "storm", "pixel", "dream", "fusion", "byte", "planet", "wave" };

        String randomPrefix = prefixes[random.nextInt(prefixes.length)];
        String randomSuffix = suffixes[random.nextInt(suffixes.length)];
        String randomDomain = domains[random.nextInt(domains.length)];

        String email = randomPrefix + randomSuffix + "@" + randomDomain;

        // Check if email already exists
        while (userRepository.existsByEmail(email)) {
            randomPrefix = prefixes[random.nextInt(prefixes.length)];
            randomSuffix = suffixes[random.nextInt(suffixes.length)];
            randomDomain = domains[random.nextInt(domains.length)];
            email = randomPrefix + randomSuffix + "@" + randomDomain;
        }

        return email;
    }

    private String generateRandomName() {
        String[] names = { "John", "Jane", "Alex", "Emily", "Michael", "Sophia", "William", "Olivia", "Jacob", "Emma", "Daniel", "Ava", "Liam", "Mia", "Ethan",
                "Noah", "Charlotte", "Benjamin", "Grace", "Henry", "Lucy", "Oliver", "Amelia", "James", "Ella", "David", "Chloe", "Joseph", "Victoria",
                "Samuel", "Madison", "Jack", "Scarlett", "Andrew", "Zoe", "Matthew", "Avery", "Daniel", "Hannah", "Luke", "Elizabeth", "Christopher",
                "Natalie", "Owen", "Grace", "Nicholas", "Lily", "Ryan", "Addison", "Gabriel", "Aria", "Sebastian", "Julian", "Carter", "Evan",
                "Christian", "Jonathan", "Isaiah" };
        return names[random.nextInt(names.length)];
    }

    private String generateRandomUsername() {
        String[] prefixes = { "user", "player", "member", "guest", "admin", "super", "mega", "cool", "awesome", "ultimate", "pro", "master", "ninja", "gamergirl",
                "rockstar", "champion", "hero", "king", "queen", "star", "wizard", "genius", "beast", "guru", "phantom", "pioneer", "tiger", "wolf",
                "eagle", "hawk", "lion", "panther", "warrior", "sprinter", "viper", "savage", "expert", "gamer", "hunter", "legend", "proplayer",
                "darkknight", "theone", "cyber", "tech", "x", "fire", "ice", "storm", "pixel", "dream", "fusion", "byte", "planet", "wave",
                "phoenix", "nova", "spirit", "shadow", "galaxy", "infinity", "cosmic", "neon", "electric", "mystic", "crimson", "sunrise", "twilight" };
        String[] suffixes = { "123", "456", "789", "abc", "xyz", "00", "11", "22", "33", "44", "55", "66", "77", "88", "99", "alpha", "beta", "omega", "gamer",
                "expert", "master", "ninja", "warrior", "king", "queen", "star", "wizard", "genius", "champion", "hero", "legend", "hunter",
                "pro", "savage", "beast", "guru", "viper", "sprinter", "phantom", "pioneer", "tiger", "wolf", "eagle", "hawk", "lion", "panther",
                "ace", "cyber", "tech", "x", "fire", "ice", "storm", "pixel", "dream", "fusion", "byte", "planet", "wave" };

        String randomPrefix = prefixes[random.nextInt(prefixes.length)];
        String randomSuffix = suffixes[random.nextInt(suffixes.length)];

        String username = randomPrefix + randomSuffix;

        // Check if username already exists
        while (userRepository.existsByUsername(username)) {
            randomPrefix = prefixes[random.nextInt(prefixes.length)];
            randomSuffix = suffixes[random.nextInt(suffixes.length)];
            username = randomPrefix + randomSuffix;
        }

        return username;
    }

}