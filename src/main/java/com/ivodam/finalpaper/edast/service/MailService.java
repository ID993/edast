package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.MailDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.mapstruct.control.MappingControl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {

    private UserService userService;
    private JavaMailSender mailSender;

    public String generateResetPasswordLink() {
        var token = RandomStringUtils.randomAlphanumeric(20);
        return "http://localhost:8080/reset-password?token=" + token;
    }

    public void sendRequest(MailDto mailDto) throws MessagingException {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var user = (User)auth.getPrincipal();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject(mailDto.getSubject());
            helper.setFrom("ivo@das.hr");
            helper.setTo(mailDto.getTo());
            helper.setText("<b>Tech Market - no reply</b><br><br>" + mailDto.getMessage()
                    + "<br><br><b>Full name: "
                    + user.getName() + "</b><br><b>Email: "
                    + user.getEmail(), true);
            mailSender.send(message);
        }
    }

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

    private static final MailDto mail = new MailDto("joe.daminew@gmail.com", "ivo.damjanovic@live.com", "Test", "Test");

    @Async
    public void sendMail() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
        helper.setTo(mail.getTo());
        helper.setText("<b>e-dast - no reply</b><br>" + mail.getMessage(), true);
        mailSender.send(message);
    }


}
