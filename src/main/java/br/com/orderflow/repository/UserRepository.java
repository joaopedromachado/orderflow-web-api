package br.com.orderflow.repository;

import br.com.orderflow.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsernameIgnoreCase(String username);

    boolean existsUserByUsernameOrEmail(String username, String email);
}
