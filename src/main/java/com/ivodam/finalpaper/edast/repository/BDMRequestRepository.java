package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BDMRequestRepository extends JpaRepository<BDMRequest, UUID> {
        List<BDMRequest> findAllByUserId(UUID userId);

        List<BDMRequest> searchAllByFullNameContainingIgnoreCase(String fullName);

        @Query("SELECT e FROM bdm_request e WHERE e.user.id IN (SELECT u.id FROM users u WHERE u.id = :userId) AND (LOWER(e.bdmSelection) LIKE %:keyword% OR LOWER(e.fullName) LIKE %:keyword%)")
        Page<BDMRequest> searchAllByKeyword(String keyword, UUID userId, Pageable pageable);





}
