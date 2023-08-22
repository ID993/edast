package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.Reservation;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {


    boolean existsByUserIdAndDateOfReservation(UUID userId, String dateOfReservation);
    @Query("SELECT r FROM reservation r WHERE r.user.id = ?1 AND (r.dateOfReservation LIKE %?2% OR LOWER(r.fondSignature) LIKE LOWER(CONCAT('%', ?2, '%')))")
    Page<Reservation> findAllByUserId(UUID userId, String keyword, Pageable pageable);


    @Query("SELECT r FROM reservation r WHERE r.dateOfReservation = ?1 AND (r.dateOfReservation LIKE %?2% OR LOWER(r.fondSignature) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(r.user.name) LIKE LOWER(CONCAT('%', ?2, '%')))")
    Page<Reservation> findAllByDateOfReservation(String date, String keyword, Pageable pageable);


    @Query("SELECT r FROM reservation r WHERE r.dateOfReservation LIKE %?1% OR LOWER(r.fondSignature) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(r.user.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    @NotNull Page<Reservation> findAll(String keyword, @NotNull Pageable pageable);

}
