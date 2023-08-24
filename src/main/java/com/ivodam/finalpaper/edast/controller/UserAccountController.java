package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserAccountController {

    private final UserService userService;
    private final PasswordHandler passwordHandler;
    private final UserMapper userMapper;

    @GetMapping("/account/{id}")
    public String getAccount(@PathVariable UUID id, Model model) throws AppException {
        var user = userService.findById(id);
        model.addAttribute("user", user);
        return "account/account";
    }

    @GetMapping("account/edit/{id}")
    public String editUser(@PathVariable UUID id, Model model) throws AppException {
        var user = userMapper.userToUserDto(userService.findById(id));
        model.addAttribute("user", user);
        model.addAttribute("roles", Enums.Roles.values());
        return "account/account-edit";
    }

    @PostMapping("account/edit/{id}")
    public String updateUser(@PathVariable UUID id,
                             @ModelAttribute("user") UserDto userDto) throws AppException {
        userService.updateUserAccount(userDto);
        return "redirect:/account/" + id;
    }


    @GetMapping("/account/delete/{id}")
    public String deleteUserById(@PathVariable UUID id) throws AppException {
        userService.deleteById(id);
        return "redirect:/logout";
    }


    @GetMapping("/account/change-password")
    public String changePassword() {
        return "password/change-password";
    }

    @PostMapping("/account/change-password")
    public String postChangePassword(@RequestParam String oldPassword,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     Model model) throws AppException, IOException {
        var user = userService.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if (!passwordHandler.checkIfValidOldPassword(user, oldPassword)) {
            model.addAttribute("message", "Wrong old password");
            return "password/change-password";
        } else if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "password/change-password";
        }
        else if (!passwordHandler.isValid(password)) {
            model.addAttribute("message", "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character");
            return "password/change-password";
        }
        userService.updatePassword(user, password);
        return "redirect:/account/" + user.getId();
    }
}
