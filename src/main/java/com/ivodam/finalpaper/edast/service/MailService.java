package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.MailDto;
import com.ivodam.finalpaper.edast.entity.Reservation;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailService(
            UserService userService,
            JavaMailSender mailSender,
            @Value("${app.mail.from:no-reply@example.com}") String fromAddress) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }


    @Async
    public void sendReservationConfirmation(
            String email,
            Reservation reservation)
            throws MessagingException, AppException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setSubject(
                "e-dast - Reservation confirmed - "
                        + reservation.getDateOfReservation());
        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setText(
                "Dear, " + email + "!<br><br>"
                        + "Your reservation is confirmed for "
                        + reservation.getDateOfReservation()
                        + ".<br><br><h4>You have reserved:</h4>"
                        + "<b>Fond/collection</b>: "
                        + reservation.getFondSignature()
                        + ".<br><b>Technical units</b>: "
                        + reservation.getTechnicalUnits()
                        + "<br><br>We hope you'll find what you're looking for."
                        + "<br><br><b>Important notice:</b> "
                        + "It is possible to change the reservation no later "
                        + "than 2 days before the reservation."
                        + "<br><br>Regards,<br><br>eDAST",
                true);

        mailSender.send(message);
    }

    @Async
    public void sendEmailAttachment(
            String subject,
            String message,
            String fromEmailAddress,
            String toEmailAddress,
            boolean htmlMail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromAddress);
            helper.setReplyTo(fromEmailAddress);
            helper.setTo(toEmailAddress);
            helper.setSubject(subject);

            if (htmlMail) {
                helper.setText(
                        "<html><body>Dear,<br><br>"
                                + message
                                + "<br><br>Regards,<br>"
                                + userService.findByEmail(fromEmailAddress)
                                        .getName()
                                + "</body></html>",
                        true);
            } else {
                helper.setText(message);
            }

            mailSender.send(mimeMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}