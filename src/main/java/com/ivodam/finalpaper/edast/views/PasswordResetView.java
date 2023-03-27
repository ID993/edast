package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class PasswordResetView {

    private final UserService userService;

    private MailService mailService;

    @GetMapping("/forgot-password")
    public String resetPassword() {
        return "forgot-password";
    }


    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String email) throws MessagingException, AppException {
        mailService.sendPasswordResetLink(email);
        return "forgot-password-success";
    }

    @GetMapping("/forgot-password/reset/{id}")
    public String changeForgottenPasswordGet(@PathVariable UUID id) {
        return "forgot-password-reset";
    }

    @PostMapping("/forgot-password/reset/{id}")
    public String changeForgottenPasswordPost(@PathVariable UUID id, @RequestParam String password) throws AppException {
        var user = userService.findById(id);
        userService.updatePassword(user, password);
        return "redirect:/login";
    }
}
