package ogami_api.ogani_website.auth.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests authentication, registration, and user retrieval logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .userId(1)
                .username("testuser")
                .email("test@example.com")
                .password("$2a$10$encodedPassword")
                .fullName("Test User")
                .phoneNumber("081234567890")
                .birthDate(LocalDate.of(1990, 1, 15))
                .age(35)
                .address("Test Address")
                .role(UserRole.CUSTOMER)
                .build();

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
        registerRequest.setPhoneNumber("081234567890");
        registerRequest.setBirthDate(LocalDate.of(1995, 5, 20));
        registerRequest.setAddress("New Address");
    }

    @Test
    @DisplayName("Login with valid credentials should return LoginResponse with token")
    void login_WithValidCredentials_ReturnsLoginResponse() {
        // Given: user exists and password matches
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("test-jwt-token");
        when(jwtUtils.getExpirationInSeconds()).thenReturn(86400L);

        // When: login is called
        LoginResponse response = authService.login(loginRequest);

        // Then: return valid login response
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(1);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(response.getExpiresIn()).isEqualTo(86400L);

        // Verify interactions
        verify(userRepository, times(1)).findByUsernameOrEmail(anyString(), anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtUtils, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Login with invalid password should throw BadCredentialsException")
    void login_WithInvalidPassword_ThrowsBadCredentialsException() {
        // Given: user exists but password doesn't match
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then: login throws BadCredentialsException
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid password");

        // Verify no token was generated
        verify(jwtUtils, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Login with non-existent user should throw DataNotFoundException")
    void login_WithNonExistentUser_ThrowsDataNotFoundException() {
        // Given: user doesn't exist
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // When & Then: login throws DataNotFoundException
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("User not found");

        // Verify password check never happened
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Register with valid data should create user successfully")
    void register_WithValidData_CreatesUser() {
        // Given: email doesn't exist, password encoder works
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        
        User savedUser = User.builder()
                .userId(2)
                .username("newuser")
                .email("new@example.com")
                .password("$2a$10$encodedPassword")
                .fullName("New User")
                .phoneNumber("081234567890")
                .birthDate(LocalDate.of(1995, 5, 20))
                .age(29) // Auto-calculated
                .address("New Address")
                .role(UserRole.CUSTOMER)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When: register is called
        UserResponse response = authService.register(registerRequest);

        // Then: user is created successfully
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(2);
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getFullName()).isEqualTo("New User");
        assertThat(response.getAge()).isEqualTo(29);
        assertThat(response.getRole()).isEqualTo(UserRole.CUSTOMER);

        // Verify interactions
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register with existing email should throw DuplicateDataException")
    void register_WithExistingEmail_ThrowsDuplicateDataException() {
        // Given: email already exists
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then: register throws DuplicateDataException
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateDataException.class)
                .hasMessageContaining("Email already exists");

        // Verify user was not saved
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Get current user with valid ID should return UserResponse")
    void getCurrentUser_WithValidId_ReturnsUserResponse() {
        // Given: user exists
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When: getCurrentUser is called
        UserResponse response = authService.getCurrentUser(1);

        // Then: return user data
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getPhoneNumber()).isEqualTo("081234567890");
        assertThat(response.getAge()).isEqualTo(35);
        assertThat(response.getRole()).isEqualTo(UserRole.CUSTOMER);

        // Verify repository was called
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Get current user with invalid ID should throw DataNotFoundException")
    void getCurrentUser_WithInvalidId_ThrowsDataNotFoundException() {
        // Given: user doesn't exist
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then: getCurrentUser throws DataNotFoundException
        assertThatThrownBy(() -> authService.getCurrentUser(999))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findById(999);
    }
}
