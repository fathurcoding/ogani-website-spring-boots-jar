package ogami_api.ogani_website.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ogami_api.ogani_website.auth.dto.LoginRequest;
import ogami_api.ogani_website.auth.dto.LoginResponse;
import ogami_api.ogani_website.auth.dto.RegisterRequest;
import ogami_api.ogani_website.auth.dto.UserResponse;
import ogami_api.ogani_website.auth.service.AuthService;
import ogami_api.ogani_website.auth.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller untuk Authentication API.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    /**
     * POST /api/auth/login - User login.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - User: {}", request.getUsernameOrEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register - Register user baru.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
         log.info("POST /api/auth/register - User: {}", request.getUsername());
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/auth/me - Get current user dari JWT token.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        
        if (!jwtUtils.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = jwtUtils.getUserIdFromToken(token);
        UserResponse response = authService.getCurrentUser(userId);
        
        return ResponseEntity.ok(response);
    }
}
