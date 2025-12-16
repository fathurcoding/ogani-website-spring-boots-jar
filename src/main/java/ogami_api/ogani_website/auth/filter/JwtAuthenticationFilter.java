package ogami_api.ogani_website.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ogami_api.ogani_website.auth.util.JwtUtils;
import ogami_api.ogani_website.user.model.User;
import ogami_api.ogani_website.user.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter.
 * Intercepts every request dan validate JWT token.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt != null && jwtUtils.validateToken(jwt)) {
                Integer userId = jwtUtils.getUserIdFromToken(jwt);

                // Load user dari database
                User user = userRepository.findById(userId).orElse(null);

                if (user != null) {
                    // Create authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,  // Principal (userId)
                                    null,    // Credentials (no password needed)
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                                    )
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication ke Security Context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT valid, user {} authenticated with role {}", userId, user.getRole());
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token dari Authorization header.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
