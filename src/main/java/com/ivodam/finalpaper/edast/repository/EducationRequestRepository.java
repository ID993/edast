package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.EducationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EducationRequestRepository extends JpaRepository<EducationRequest, UUID> {
        List<EducationRequest> findAllByUserId(UUID userId);

        @Query("SELECT e FROM education_request e WHERE e.user.id IN (SELECT u.id FROM users u WHERE u.id = :userId) AND (LOWER(e.educationSelection) LIKE %:keyword% OR LOWER(e.fullName) LIKE %:keyword% OR LOWER(e.schoolName) LIKE %:keyword%)")
        Page<EducationRequest> searchAllByKeyword(String keyword, UUID userId, Pageable pageable);
}
