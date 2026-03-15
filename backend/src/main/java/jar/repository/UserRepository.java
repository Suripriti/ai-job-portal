package jar.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jar.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Check if a user exists by email
    boolean existsByEmail(String email);

    // Find a user by email (used in login)
    Optional<User> findByEmail(String email);
}
