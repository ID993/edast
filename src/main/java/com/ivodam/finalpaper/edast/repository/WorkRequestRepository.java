package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WorkRequestRepository extends JpaRepository<WorkRequest, UUID> {
        List<WorkRequest> findAllByUserId(UUID userId);

        @Query("SELECT e FROM work_request e WHERE (LOWER(e.workSelection) LIKE %:keyword% OR LOWER(e.employersName)LIKE %:keyword% OR LOWER(e.fullName)LIKE %:keyword%) AND e.user.id IN (SELECT u.id FROM users u WHERE u.id = :userId)")
        List<WorkRequest> searchAllByKeyword(String keyword, UUID userId);
}
