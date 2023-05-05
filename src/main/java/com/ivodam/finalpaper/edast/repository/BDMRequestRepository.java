package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BDMRequestRepository extends JpaRepository<BDMRequest, UUID> {
        List<BDMRequest> findAllByUserId(UUID userId);
}
