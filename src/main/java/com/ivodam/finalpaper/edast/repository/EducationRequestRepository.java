package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EducationRequestRepository extends JpaRepository<EducationRequest, UUID> {
        List<EducationRequest> findAllByUserId(UUID userId);

        @Query("SELECT e FROM education_request e WHERE (LOWER(e.educationSelection) LIKE %:keyword% OR LOWER(e.fullName) LIKE %:keyword% OR LOWER(e.schoolName) LIKE %:keyword%) AND e.user.id IN (SELECT u.id FROM users u WHERE u.id = :userId)")
        List<EducationRequest> searchAllByKeyword(String keyword, UUID userId);
}
