package ogami_api.ogani_website.user.service;

import lombok.RequiredArgsConstructor;
import ogami_api.ogani_website.exception.DataAlreadyExistsException;
import ogami_api.ogani_website.exception.DataNotFoundException;
import ogami_api.ogani_website.user.model.User;
import ogami_api.ogani_website.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer untuk User.
 * Business logic untuk user management dan authentication.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users (admin only).
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID.
     */
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User", id));
    }

    /**
     * Get user by email (untuk login).
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User dengan email " + email + " tidak ditemukan"));
    }

    /**
     * Create new user (registration).
     */
    public User createUser(User user) {
        // Validasi: email wajib
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email wajib diisi");
        }

        // Validasi: username wajib
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username wajib diisi");
        }

        // Validasi: password wajib
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password wajib diisi");
        }

        // Validasi: email unique
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataAlreadyExistsException("Email " + user.getEmail() + " sudah terdaftar");
        }

        // Validasi: username unique
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DataAlreadyExistsException("Username " + user.getUsername() + " sudah digunakan");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Update user profile.
     */
    public User updateUser(Integer id, User updatedUser) {
        User existing = getUserById(id);

        // Update fields (password tidak bisa diupdate via method ini)
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isBlank()) {
            // Check uniqueness jika username berubah
            if (!existing.getUsername().equals(updatedUser.getUsername()) &&
                    userRepository.existsByUsername(updatedUser.getUsername())) {
                throw new DataAlreadyExistsException("Username " + updatedUser.getUsername() + " sudah digunakan");
            }
            existing.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getPhoneNumber() != null) {
            existing.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        return userRepository.save(existing);
    }

    /**
     * Update password (separate method untuk keamanan).
     */
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        User user = getUserById(id);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Password lama tidak sesuai");
        }

        // Encode dan update password baru
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Delete user.
     */
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new DataNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
