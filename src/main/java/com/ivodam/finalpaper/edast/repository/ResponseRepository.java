package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<Response, UUID> {

  Optional<Response> findByRegistryBookId(UUID registryBookId);

  List<Response> findAllByUserId(UUID userId);

  long countByReadAndUserId(boolean read, UUID userId);
}
