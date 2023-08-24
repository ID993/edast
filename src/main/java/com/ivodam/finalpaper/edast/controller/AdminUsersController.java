package com.ivodam.finalpaper.edast.controller;


import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class AdminUsersController {


    private final UserService userService;
    private final RegistryBookService registryBookService;
    private final UserMapper userMapper;

//    @RequestMapping("/search-users")
//    public String searchUsers(@RequestParam(defaultValue = "") String search,
//                              @RequestParam(defaultValue = "0") int page,
//                              @RequestParam(defaultValue = "6") int size,
//                              @RequestParam(defaultValue = "All") String type,
//                              Model model) {
//        var pageable = PageRequest.of(page, size);
//        var users = userService.findAllByEmailOrNameOrJobTitleContainingIgnoreCase(search, pageable);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("users", users);
//        model.addAttribute("type", type);
//        model.addAttribute("search", search);
//        return "admin/users-all";
//    }

    @GetMapping("/users")
    public String getAll(@RequestParam(defaultValue = "") String search,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "6") int size,
                         @RequestParam(defaultValue = "All") String type,
                         Model model) {
        var pageable = PageRequest.of(page, size);
        switch (type) {
            case "Admin" ->
                    model.addAttribute("users", userService.findAllByRole(Enums.Roles.ROLE_ADMIN, pageable));
            case "User" ->
                    model.addAttribute("users", userService.findAllByRole(Enums.Roles.ROLE_USER, pageable));
            case "Employee" ->
                    model.addAttribute("users", userService.findAllByRole(Enums.Roles.ROLE_EMPLOYEE, pageable));
            case "All" ->
                    model.addAttribute("users", userService.findAllByEmailOrNameOrJobTitleContainingIgnoreCase(search, pageable));
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("type", type);
        model.addAttribute("search", search);
        return "admin/users-all";
    }


    @GetMapping("/users/all/{id}")
    public String getUserById(Model model, @PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/users-update-roles";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserById(Model model, @PathVariable UUID id) throws AppException {
        var user = userMapper.userToUserDto(userService.findById(id));
        model.addAttribute("user", user);
        model.addAttribute("jobs", Enums.JobPosition.values());
        return "admin/admin-user-edit";
    }

    @PostMapping("/users/edit/{id}")
    public String editUserById(@PathVariable UUID id,
                               @ModelAttribute("user") UserDto user,
                               @RequestParam String job) throws AppException {
        user.setJobTitle(job);
        userService.updateUserAccount(user);
        return "redirect:/users/all/{id}";
    }

    @GetMapping("/users/add-admin/{id}")
    public String addAdmin(@PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        user.setRole(Enums.Roles.ROLE_ADMIN);
        userService.updateRole(user);
        registryBookService.divideRequests(id);
        return "redirect:/users/all/{id}";
    }

    @GetMapping("/users/add-employee/{id}")
    public String addEmployee(@PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        user.setRole(Enums.Roles.ROLE_EMPLOYEE);
        userService.updateRole(user);
        return "redirect:/users/all/{id}";
    }

    @GetMapping("admin/account/delete/{id}")
    public String deleteUserById(@PathVariable UUID id) throws AppException {
        var user = userService.findById(id);
        var role = user.getRole().getDisplayName();
        if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            user.setRole(Enums.Roles.ROLE_USER);
            userService.updateRole(user);
            registryBookService.divideRequests(id);
        } else {
            userService.deleteById(id);
        }
        return "redirect:/users?type=" + role;
    }

}
