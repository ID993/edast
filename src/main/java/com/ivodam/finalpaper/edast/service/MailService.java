package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.MailDto;
import com.ivodam.finalpaper.edast.entity.Reservation;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MailService {

    private UserService userService;
    private JavaMailSender mailSender;


    @Async
    public void sendPasswordResetLink(String email) throws MessagingException, AppException {
        var user = userService.findByEmail(email);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setSubject("e-dast - password reset");
        helper.setFrom("joe.daminew@gmail.com");
        helper.setTo(email);
        helper.setText("<b>e-dast - reset password</b><br><br> Hello, " + email + "!<br><br>"
                + "Follow the link to reset your password. <br> " +
                "<a href=\"http://localhost:8080/forgot-password/reset/" + user.getId() + "\">Reset password</a>", true);
        mailSender.send(message);
    }

    @Async
    public void sendReservationConfirmation(String email, Reservation reservation) throws MessagingException, AppException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setSubject("e-dast - Reservation confirmed - " + reservation.getDateOfReservation());
        helper.setFrom("joe.daminew@gmail.com");
        helper.setTo(email);
        helper.setText("Dear, " + email + "!<br><br>"
                + "Your reservation is confirmed for " + reservation.getDateOfReservation()
                + ".<br><br><h4>You have reserved:</h4><b>Fond/collection</b>: "
                + reservation.getFondSignature() + ". <br><b>Technical units</b>: "
                + reservation.getTechnicalUnits() + "<br><br>We hope You'll find what You're looking for.<br>" +
                "<br><b>Important notice:</b>It is possible to change the reservation no later than 2 days before the reservation."
                + "<br><br>Regards,<br><br>eDAST", true);
        mailSender.send(message);
    }

    @Async
    public void sendEmailAttachment(final String subject, final String message, final String fromEmailAddress,
                                    final String toEmailAddresses, final boolean isHtmlMail) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(fromEmailAddress);
                helper.setTo(toEmailAddresses);
                helper.setSubject(subject);
    
                if (isHtmlMail) {
                    helper.setText("<html><body> Dear <br><br>" + message
                            + "<br><br>Regards,<br>"
                            + userService.findByEmail(fromEmailAddress).getName() + "</html></body>", true);
                } else {
                    helper.setText(message);
                }
//                for (MultipartFile file: multipartFiles) {
//                    helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
//                }
                mailSender.send(mimeMessage);
                System.out.println("Email sending complete.");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
