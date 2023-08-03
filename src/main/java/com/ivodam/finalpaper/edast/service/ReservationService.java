package com.ivodam.finalpaper.edast.service;


import com.ivodam.finalpaper.edast.entity.Reservation;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;


    public Reservation create(Reservation reservation) {
        reservation.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return reservationRepository.save(reservation);
    }

    public boolean canUpdateReservation(String dateOfReservation) {
        var currentDate = LocalDate.now();
        var dateOfReservationParsed = LocalDate.parse(dateOfReservation, DateTimeFormatter.ofPattern("dd.MM.yyyy."));
        var oneDayBeforeReservationDate = dateOfReservationParsed.minusDays(1);
        return currentDate.isBefore(oneDayBeforeReservationDate);
    }

    public Reservation update(Reservation reservation) throws AppException {
        reservation.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return reservationRepository.save(reservation);
    }

    public void deleteById(UUID id) throws AppException {
        var reservation =  reservationRepository.findById(id).orElseThrow(() ->
                new AppException("Reservation with id: " + id + " not found", HttpStatus.NOT_FOUND));
        reservationRepository.deleteById(id);
    }

    public Reservation findById(UUID id) throws AppException {
        return reservationRepository.findById(id).orElseThrow(() ->
                new AppException("Reservation with id: " + id + " not found", HttpStatus.NOT_FOUND));
    }


    public Page<Reservation> findAll(String keyword, Pageable pageable) {
        return reservationRepository.findAll(keyword, pageable);
    }

    public Page<Reservation> findAllByUserId(UUID userId, String keyword, Pageable pageable) {
        return reservationRepository.findAllByUserId(userId, keyword, pageable);
    }

    public Page<Reservation> findAllByDateOfReservation(String keyword, Pageable pageable) {
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
        return reservationRepository.findAllByDateOfReservation(date, keyword, pageable);
    }

}
