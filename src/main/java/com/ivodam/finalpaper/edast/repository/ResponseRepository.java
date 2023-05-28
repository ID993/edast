package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, UUID> {

    Response findByRequestId(UUID requestId);

    List<Response> findAllByUserId(UUID userId);

    long countByReadAndUserId(boolean read, UUID userId);
}
