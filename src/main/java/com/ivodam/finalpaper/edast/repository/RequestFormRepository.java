package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.RequestForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestFormRepository extends JpaRepository<RequestForm, UUID> {
    List<RequestForm> findByUserId(UUID userId);


}

