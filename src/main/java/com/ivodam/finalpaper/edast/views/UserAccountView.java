package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserAccountView {

    private final UserService userService;

    @GetMapping("/account")
    public String getAccount(Model model) throws AppException {
        var user = userService.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("account/edit/{id}")
    public String editUser(@PathVariable UUID id, Model model) throws AppException {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", Enums.Roles.values());
        return "edit-user";
    }

    @PostMapping("account/edit/{id}")
    public String updateUser(Model model,
                             @ModelAttribute("user") UserDto userDto,
                             HttpServletRequest request,
                             Errors errors) throws AppException {
        userService.updateUserAccount(userDto);
        return "redirect:/account";
    }


    @GetMapping("/account/delete/{id}")
    public String deleteUserById(@PathVariable UUID id) {
        userService.deleteById(id);
        return "redirect:/logout";
    }


    @GetMapping("/account/change-password")
    public String changePassword() {
        return "change-password";
    }

    @PostMapping("/account/change-password")
    public String postChangePassword(@RequestParam String oldPassword,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword) throws AppException, IOException {
        var user = userService.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            throw new IOException("Wrong old password");
        } else if (!password.equals(confirmPassword)) {
            throw new IOException("Passwords do not match");
        }
        userService.updatePassword(user, password);
        return "redirect:/account";
    }
}
