package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.MailDto;
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
    public void sendRequest(MailDto mailDto) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setSubject(mailDto.getSubject());
        helper.setFrom(mailDto.getFrom());
        helper.setTo(mailDto.getTo());
        helper.setText(mailDto.getMessage());
        mailSender.send(message);

    }

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



//    @Qualifier("taskExecutor")
//    private TaskExecutor taskExecutor;

    @Async
    public void sendEmailAttachment(final String subject, final String message, final String fromEmailAddress,
                                    final String toEmailAddresses, final boolean isHtmlMail,
                                    MultipartFile[] multipartFiles) {
        //taskExecutor.execute(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(fromEmailAddress);
                helper.setTo(toEmailAddresses);
                helper.setSubject(subject);
    
                if (isHtmlMail) {
                    helper.setText("<html><body>" + message + "</html></body>", true);
                } else {
                    helper.setText(message);
                }
                for (MultipartFile file: multipartFiles) {
                    helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
                }
                mailSender.send(mimeMessage);
                System.out.println("Email sending complete.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        //});
    }
}
