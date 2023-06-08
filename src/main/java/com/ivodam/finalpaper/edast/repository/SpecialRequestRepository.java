package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpecialRequestRepository extends JpaRepository<SpecialRequest, UUID> {
        List<SpecialRequest> findAllByUserId(UUID userId);

        @Query("SELECT e FROM special_request e WHERE (LOWER(e.creatorName) LIKE %:keyword% OR LOWER(e.title) LIKE %:keyword%) AND e.user.id IN (SELECT u.id FROM users u WHERE u.id = :userId)")
        List<SpecialRequest> searchAllByKeyword(String keyword, UUID userId);
}
