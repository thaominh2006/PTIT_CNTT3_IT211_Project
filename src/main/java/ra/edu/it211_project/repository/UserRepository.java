package ra.edu.it211_project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.edu.it211_project.entity.RoleEnum;
import ra.edu.it211_project.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findByRoleAndFullNameContainingIgnoreCase(RoleEnum role, String fullName, Pageable pageable);
    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
