package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class UpdateUser {


    private UserService userService;



    @GetMapping("/users/list")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users-list";
    }

    @GetMapping("/users/list/{id}")
    public String getUserById(Model model, @PathVariable long id) throws AppException {
        var user = userService.findById(id);
        model.addAttribute("user", user);
        return "users-update-roles";
    }


    @GetMapping("/users/add-admin/{id}")
    public String addAdmin(@PathVariable long id) throws AppException {
        var user = userService.findById(id);
        user.setRole(Enums.Roles.ROLE_ADMIN);
        userService.update(user);
        return "redirect:/users/list/{id}";
    }

    @GetMapping("/users/add-employee/{id}")
    public String addEmployee(@PathVariable long id) throws AppException {
        var user = userService.findById(id);
        user.setRole(Enums.Roles.ROLE_EMPLOYEE);
        userService.update(user);
        return "redirect:/users/list/{id}";
    }

    @GetMapping("/account")
    public String getAccount(Model model) throws AppException {
        var user = userService.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("account/{id}/edit")
    public String editUser(@PathVariable long id, Model model) throws AppException {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", Enums.Roles.values());
        return "edit-user";
    }

    @PostMapping("account/{id}/edit")
    public String updateUser(Model model,
                           @ModelAttribute("user") UserDto userDto,
                           HttpServletRequest request,
                           Errors errors) throws AppException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = (User) auth.getPrincipal();
        userService.updateUserAccount(userDto);
        return "redirect:/user/" + user.getId() + "/edit";
    }


    @GetMapping("/account/delete/{id}")
    public String deleteUserById(@PathVariable long id) {
        userService.deleteById(id);
        return "redirect:/users/list";
    }


    //PASSWORD RESET

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

    @GetMapping("/forgot-password")
    public String resetPassword() {
        return "forgot-password";
    }

    private MailService mailService;
    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String email) throws MessagingException, AppException {
        mailService.sendPasswordResetLink(email);
        return "forgot-password-success";
    }

    @GetMapping("/forgot-password/reset/{id}")
    public String changeForgottenPasswordGet(@PathVariable long id) {
        return "forgot-password-reset";
    }

    @PostMapping("/forgot-password/reset/{id}")
    public String changeForgottenPasswordPost(@PathVariable long id, @RequestParam String password) throws AppException {
        var user = userService.findById(id);
        userService.updatePassword(user, password);
        return "redirect:/login";
    }

}
