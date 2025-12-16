package ogami_api.ogani_website.user.repository;

import ogami_api.ogani_website.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository untuk User entity.
 * Provides CRUD operations dan custom queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find user by email (untuk login).
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email sudah terdaftar.
     */
    Boolean existsByEmail(String email);
    
    /**
     * Check if username sudah digunakan.
     */
    Boolean existsByUsername(String username);
}
