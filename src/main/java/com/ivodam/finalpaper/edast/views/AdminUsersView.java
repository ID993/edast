package com.ivodam.finalpaper.edast.views;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class AdminUsersView {


    private final UserService userService;
    private final RegistryBookService registryBookService;

    @RequestMapping("/search-users")
    public String searchUsers(Model model,
                              @RequestParam String name,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size) {
        var pageable = PageRequest.of(page, size);
        model.addAttribute("users", userService.findAllByNameContainingIgnoreCase(name, pageable));
        model.addAttribute("currentPage", page);
        return "admin/users-all";
    }

    @GetMapping("/users/all")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "6") int size) {
        var pageable = PageRequest.of(page, size);
        model.addAttribute("users", userService.findAll(pageable));
        model.addAttribute("currentPage", page);
        return "admin/users-all";
    }

    @GetMapping("/users/citizens")
    public String getAllUsers(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size) {
        var pageable = PageRequest.of(page, size);
        model.addAttribute("users", userService.findAllByRole(Enums.Roles.ROLE_USER, pageable));
        model.addAttribute("currentPage", page);
        return "admin/users-all";
    }

    @GetMapping("/users/employees")
    public String getAllEmployees(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {
        var pageable = PageRequest.of(page, size);
        model.addAttribute("users", userService.findAllByRole(Enums.Roles.ROLE_EMPLOYEE, pageable));
        model.addAttribute("currentPage", page);
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
        var user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("jobs", Enums.JobPosition.values());
        return "admin/admin-user-edit";
    }

    @PostMapping("/users/edit/{id}")
    public String editUserById(@PathVariable UUID id,
                               @Valid @ModelAttribute("user") UserDto user,
                               @RequestParam String job) throws AppException {
        user.setJobTitle(job);
        System.out.println(user);
        userService.updateUserAccount(user);
        return "redirect:/users/all/" +id;
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
        user.setRole(Enums.Roles.ROLE_USER);
        userService.updateRole(user);
        registryBookService.divideRequests(id);
        //userService.deleteById(id);
        return "redirect:/users/all";
    }

}
