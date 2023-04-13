package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByNameContainingIgnoreCase(String search);


    List<User> findAllByRoleAndJobTitle(Enums.Roles role, String jobTitle);
}
