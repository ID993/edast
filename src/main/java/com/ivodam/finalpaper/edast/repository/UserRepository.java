package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u.role, COUNT(u) FROM users u GROUP BY u.role")
    List<Object[]> getTotalUsersByRole();

    Optional<User> findByEmail(String email);
    @NotNull
    Page<User> findAll(@NotNull Pageable pageable);
    boolean existsByEmail(String email);

    //Page<User> findAllByNameContainingIgnoreCase(String search, Pageable pageable);

    @Query("SELECT u FROM users u WHERE LOWER(u.name) LIKE %:search% OR LOWER(u.email) LIKE %:search%")
    Page<User> findAllByEmailOrNameContainingIgnoreCase(String search, Pageable pageable);

    Page<User> findAllByRole(Enums.Roles role, Pageable pageable);
    List<User> findAllByRoleAndJobTitle(Enums.Roles role, String jobTitle);
}
