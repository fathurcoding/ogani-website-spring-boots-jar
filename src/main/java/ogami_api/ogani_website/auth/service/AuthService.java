package ogami_api.ogani_website.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ogami_api.ogani_website.auth.dto.LoginRequest;
import ogami_api.ogani_website.auth.dto.LoginResponse;
import ogami_api.ogani_website.auth.dto.RegisterRequest;
import ogami_api.ogani_website.auth.dto.UserResponse;
import ogami_api.ogani_website.auth.util.JwtUtils;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.exception.DuplicateDataException;
import ogami_api.ogani_website.user.model.User;
import ogami_api.ogani_website.user.model.UserRole;
import ogami_api.ogani_website.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Service untuk authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * User login dengan username  atau email.
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());

        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(
                        request.getUsernameOrEmail(),
                        request.getUsernameOrEmail())
                .orElseThrow(() -> new DataNotFoundException(
                        "User not found with username/email: " + request.getUsernameOrEmail()));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(user);

        log.info("User logged in successfully: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .expiresIn(jwtUtils.getExpirationInSeconds())
                .build();
    }

    /**
     * Register user baru dengan enhanced profile.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registration attempt for: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateDataException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateDataException("Email already exists: " + request.getEmail());
        }

        // Calculate age dari birthDate jika ada
        Integer age = request.getAge();
        if (age == null && request.getBirthDate() != null) {
            age = calculateAge(request.getBirthDate());
        }

        // Build user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .age(age)
                .address(request.getAddress())
                .role(UserRole.CUSTOMER)  // Default role
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getUsername());

        return toUserResponse(savedUser);
    }

    /**
     * Get current user dari JWT token.
     */
    public UserResponse getCurrentUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));

        return toUserResponse(user);
    }

    /**
     * Calculate age dari birth date.
     */
    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Convert User entity to UserResponse DTO.
     */
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .age(user.getAge())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}
