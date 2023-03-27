package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class AdminUsersView {


    private final UserService userService;

    @RequestMapping("/search")
    public String searchUsers(@RequestParam String name, Model model) {
        model.addAttribute("users", userService.findAllByNameContainingIgnoreCase(name));
        return "users-list";
    }

    @GetMapping("/users/list")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users-list";
    }

    @GetMapping("/users/list/{id}")
    public String getUserById(Model model, @PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        model.addAttribute("user", user);
        return "users-update-roles";
    }

    @GetMapping("/users/add-admin/{id}")
    public String addAdmin(@PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        user.setRole(Enums.Roles.ROLE_ADMIN);
        userService.update(user);
        return "redirect:/users/list/{id}";
    }

    @GetMapping("/users/add-employee/{id}")
    public String addEmployee(@PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        user.setRole(Enums.Roles.ROLE_EMPLOYEE);
        userService.update(user);
        return "redirect:/users/list/{id}";
    }

    @GetMapping("admin/account/delete/{id}")
    public String deleteUserById(@PathVariable UUID id) {
        userService.deleteById(id);
        return "redirect:/users/list";
    }

}
