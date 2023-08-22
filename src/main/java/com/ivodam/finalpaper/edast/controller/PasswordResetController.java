package com.ivodam.finalpaper.edast.controller;


import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.UserService;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class PasswordResetController {

    private final UserService userService;
    private final PasswordHandler passwordHandler;
    private MailService mailService;

    @GetMapping("/forgot-password")
    public String resetPassword() {
        return "password/forgot-password";
    }


    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String email) throws MessagingException, AppException {
        mailService.sendPasswordResetLink(email);
        return "password/forgot-password-success";
    }

    @GetMapping("/forgot-password/reset/{id}")
    public String changeForgottenPasswordGet(@PathVariable UUID id) {
        return "password/forgot-password-reset";
    }

    @PostMapping("/forgot-password/reset/{id}")
    public String changeForgottenPasswordPost(@PathVariable UUID id,
                                              @RequestParam String password,
                                              @RequestParam String confirmPassword,
                                              Model model) throws AppException {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "password/forgot-password-reset";
        } else if (!passwordHandler.isValid(password)) {
            model.addAttribute("message", "Password must contain at least 8 characters, one uppercase, one lowercase, one digit and one special character");
            return "password/forgot-password-reset";
        }
        var user = userService.findById(id);
        userService.updatePassword(user, password);
        return "redirect:/login";
    }
}
