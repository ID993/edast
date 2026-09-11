package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.Reservation;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTests {

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.yyyy.");

  @Mock private ReservationRepository reservationRepository;

  @Mock private MailService mailService;

  @InjectMocks private ReservationService reservationService;

  @Test
  void ownerCanViewOwnReservation() throws AppException {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    var result =
        reservationService.findAccessibleById(reservation.getId(), owner);

    assertThat(result).isSameAs(reservation);
  }

  @ParameterizedTest
  @EnumSource(value = Enums.Roles.class,
              names = {"ROLE_ADMIN", "ROLE_EMPLOYEE"})
  void staffCanViewAnyReservation(Enums.Roles role) throws AppException {

    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var staff = user(UUID.randomUUID(), role);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    var result =
        reservationService.findAccessibleById(reservation.getId(), staff);

    assertThat(result).isSameAs(reservation);
  }

  @Test
  void userCannotViewAnotherUsersReservation() {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var otherUser = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    assertNotFound(()
                       -> reservationService.findAccessibleById(
                           reservation.getId(), otherUser));
  }

  @Test
  void ownerCanDeleteOwnReservation() throws AppException {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    reservationService.deleteOwnedBy(reservation.getId(), owner.getId());

    verify(reservationRepository).delete(reservation);
  }

  @Test
  void userCannotDeleteAnotherUsersReservation() {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var otherUser = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    assertNotFound(()
                       -> reservationService.deleteOwnedBy(reservation.getId(),
                                                           otherUser.getId()));

    verify(reservationRepository, never()).delete(any(Reservation.class));
  }

  @Test
  void ownerCanUpdateOnlyEditableFields() throws AppException {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));
    var originalId = reservation.getId();
    var originalCreatedDate = reservation.getDateCreated();

    var changes = new Reservation();
    changes.setId(UUID.randomUUID());
    changes.setUser(user(UUID.randomUUID(), Enums.Roles.ROLE_ADMIN));
    changes.setDateCreated("01.01.1900.");
    changes.setDateOfReservation(
        LocalDate.now().plusDays(10).format(DATE_FORMAT));
    changes.setFondSignature("HR-DAST-9999");
    changes.setTechnicalUnits("Updated units");

    when(reservationRepository.findById(originalId))
        .thenReturn(Optional.of(reservation));
    when(reservationRepository.save(reservation)).thenReturn(reservation);

    var updated =
        reservationService.updateOwnedBy(originalId, owner.getId(), changes);

    assertThat(updated.getId()).isEqualTo(originalId);
    assertThat(updated.getUser()).isSameAs(owner);
    assertThat(updated.getDateCreated()).isEqualTo(originalCreatedDate);
    assertThat(updated.getDateOfReservation())
        .isEqualTo(changes.getDateOfReservation());
    assertThat(updated.getFondSignature())
        .isEqualTo(changes.getFondSignature());
    assertThat(updated.getTechnicalUnits())
        .isEqualTo(changes.getTechnicalUnits());

    verify(reservationRepository).save(reservation);
  }

  @Test
  void userCannotUpdateAnotherUsersReservation() {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var otherUser = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(3));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    assertNotFound(()
                       -> reservationService.updateOwnedBy(reservation.getId(),
                                                           otherUser.getId(),
                                                           new Reservation()));

    verify(reservationRepository, never()).save(any(Reservation.class));
  }

  @Test
  void reservationCannotBeUpdatedTooLate() {
    var owner = user(UUID.randomUUID(), Enums.Roles.ROLE_USER);
    var reservation = reservation(owner, LocalDate.now().plusDays(1));

    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    assertThatThrownBy(
        ()
            -> reservationService.updateOwnedBy(
                reservation.getId(), owner.getId(), new Reservation()))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.BAD_REQUEST));

    verify(reservationRepository, never()).save(any(Reservation.class));
  }

  private void assertNotFound(ThrowingOperation operation) {
    assertThatThrownBy(operation::run)
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));
  }

  private User user(UUID id, Enums.Roles role) {
    var user = new User();
    user.setId(id);
    user.setRole(role);
    return user;
  }

  private Reservation reservation(User owner, LocalDate date) {
    var reservation = new Reservation();
    reservation.setId(UUID.randomUUID());
    reservation.setUser(owner);
    reservation.setDateCreated("01.09.2026.");
    reservation.setDateOfReservation(date.format(DATE_FORMAT));
    reservation.setFondSignature("HR-DAST-1234");
    reservation.setTechnicalUnits("Original units");
    return reservation;
  }

  @FunctionalInterface
  private interface ThrowingOperation {
    void run() throws AppException;
  }
}