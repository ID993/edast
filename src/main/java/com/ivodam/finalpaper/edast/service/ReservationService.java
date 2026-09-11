package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.Reservation;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.ReservationRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservationService {

  private ReservationRepository reservationRepository;
  private MailService mailService;

  public boolean existsByUserAndDate(UUID userId, String dateOfReservation) {
    return reservationRepository.existsByUserIdAndDateOfReservation(
        userId, dateOfReservation);
  }

  public Reservation create(Reservation reservation)
      throws AppException, MessagingException {
    reservation.setDateCreated(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
    var r = reservationRepository.save(reservation);
    mailService.sendReservationConfirmation(reservation.getUser().getEmail(),
                                            r);
    return r;
  }

  public boolean canUpdateReservation(String dateOfReservation) {
    var currentDate = LocalDate.now();
    var dateOfReservationParsed = LocalDate.parse(
        dateOfReservation, DateTimeFormatter.ofPattern("dd.MM.yyyy."));
    var oneDayBeforeReservationDate = dateOfReservationParsed.minusDays(1);
    return currentDate.isBefore(oneDayBeforeReservationDate);
  }

  public Reservation findAccessibleById(UUID id, User currentUser)
      throws AppException {

    var reservation = findById(id);
    var role = currentUser.getRole();

    var staff =
        role == Enums.Roles.ROLE_ADMIN || role == Enums.Roles.ROLE_EMPLOYEE;

    if (!staff &&
        !Objects.equals(reservation.getUser().getId(), currentUser.getId())) {
      throw reservationNotFound();
    }

    return reservation;
  }

  public Reservation findOwnedById(UUID id, UUID userId) throws AppException {

    var reservation = findById(id);

    if (reservation.getUser() == null ||
        !Objects.equals(reservation.getUser().getId(), userId)) {
      throw reservationNotFound();
    }

    return reservation;
  }

  public Reservation findUpdatableOwnedById(UUID id, UUID userId)
      throws AppException {

    var reservation = findOwnedById(id, userId);

    if (!canUpdateReservation(reservation.getDateOfReservation())) {
      throw new AppException("Reservation can no longer be updated",
                             HttpStatus.BAD_REQUEST);
    }

    return reservation;
  }

  public Reservation updateOwnedBy(UUID id, UUID userId, Reservation changes)
      throws AppException {

    var reservation = findUpdatableOwnedById(id, userId);

    reservation.setDateOfReservation(changes.getDateOfReservation());
    reservation.setFondSignature(changes.getFondSignature());
    reservation.setTechnicalUnits(changes.getTechnicalUnits());

    return reservationRepository.save(reservation);
  }

  public void deleteOwnedBy(UUID id, UUID userId) throws AppException {

    var reservation = findOwnedById(id, userId);
    reservationRepository.delete(reservation);
  }

  public Reservation findById(UUID id) throws AppException {
    return reservationRepository.findById(id).orElseThrow(
        this::reservationNotFound);
  }

  private AppException reservationNotFound() {
    return new AppException("Reservation not found", HttpStatus.NOT_FOUND);
  }

  public Page<Reservation> findAll(String keyword, Pageable pageable) {
    return reservationRepository.findAll(keyword, pageable);
  }

  public Page<Reservation> findAllByUserId(UUID userId, String keyword,
                                           Pageable pageable) {
    return reservationRepository.findAllByUserId(userId, keyword, pageable);
  }

  public Page<Reservation> findAllByDateOfReservation(String keyword,
                                                      Pageable pageable) {
    var date =
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
    return reservationRepository.findAllByDateOfReservation(date, keyword,
                                                            pageable);
  }
}
