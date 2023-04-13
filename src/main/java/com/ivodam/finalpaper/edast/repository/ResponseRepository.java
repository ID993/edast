package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, UUID> {

    Response findByRequestId(UUID requestId);
}
