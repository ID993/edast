package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.entity.Reservation;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.ReservationService;
import com.ivodam.finalpaper.edast.utility.Utility;
import jakarta.validation.Valid;
import jdk.jshell.execution.Util;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ReservationView {

    private final ReservationService reservationService;
    private final Utility utility;


    @GetMapping("/reservations")
    public String getReservations(Model model,
                                  @RequestParam(defaultValue = "") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size,
                                  @RequestParam(defaultValue = "dateCreated") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortOrder) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = utility.getSort(sortBy, sortOrder);
        var pageable = PageRequest.of(page, size, sort);
        Page<Reservation> reservations;
        if (user.getRole().equals(Enums.Roles.ROLE_USER))
            reservations = reservationService.findAllByUserId(user.getId(), keyword , pageable);
        else
            reservations = reservationService.findAll(keyword, pageable);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reservations", reservations);
        return "reservation/reservations";
    }

    @GetMapping("/reservations/today")
    public String getReservationsOfToday(Model model,
                                  @RequestParam(defaultValue = "") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size,
                                  @RequestParam(defaultValue = "dateCreated") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortOrder) {
        var sort = utility.getSort(sortBy, sortOrder);
        var pageable = PageRequest.of(page, size, sort);
        var reservations = reservationService.findAllByDateOfReservation(keyword, pageable);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reservations", reservations);
        return "reservation/reservations";
    }


    @GetMapping("/reservation")
    public String makeReservation(Model model){
        model.addAttribute("reservation", new Reservation());
        return "reservation/make-reservation";
    }

    @PostMapping("/reservation")
    public String makeReservation(@Valid @ModelAttribute Reservation reservation,
                                  BindingResult result){
        if (result.hasErrors()) {
            return "reservation/make-reservation";
        }
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservation.setUser(user);
        var r = reservationService.create(reservation);
        return "redirect:/reservation-details/" + r.getId();
    }

    @GetMapping("/reservation-details/{id}")
    public String reservationDetails(@PathVariable UUID id,
                                     Model model) throws AppException {
        var reservation = reservationService.findById(id);
        var updatable = reservationService.canUpdateReservation(reservation.getDateOfReservation());
        model.addAttribute("reservation", reservation);
        model.addAttribute("updatable", updatable);
        return "reservation/reservation-details";
    }

    @GetMapping("/reservation/{id}/delete")
    public String deleteReservation(@PathVariable UUID id) throws AppException {
        reservationService.deleteById(id);
        return "redirect:/reservations";
    }

    @GetMapping("/reservation/{id}/edit")
    public String editReservation(@PathVariable UUID id,
                                  Model model) throws AppException {
        var reservation = reservationService.findById(id);
        model.addAttribute("reservation", reservation);
        return "reservation/edit-reservation";
    }

    @PostMapping("/reservation/{id}/edit")
    public String editReservation(@PathVariable UUID id,
                                  @Valid @ModelAttribute Reservation reservation,
                                  BindingResult result) throws AppException {
        if (result.hasErrors()) {
            return "reservation/edit-reservation";
        }
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservation.setUser(user);
        reservationService.update(reservation);
        return "redirect:/reservation-details/" + id;
    }
}
