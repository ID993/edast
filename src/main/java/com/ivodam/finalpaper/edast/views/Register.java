package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class Register {

    //implement password confirmation

    private UserService userService;


    @GetMapping("/register")
    public String getRegister(WebRequest request, Model model) {
        var user = new User();
        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute("user") User user,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   Errors errors) throws AppException {

        redirectAttributes.addFlashAttribute("message", "Failed username");
        redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        if (result.hasErrors() || userService.existsByEmail(user.getEmail())) {
            return "register";
        }
        redirectAttributes.addFlashAttribute("message", "Success");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        user.setRole(Enums.Roles.ROLE_USER);
        var createdUser = userService.create(user);
        System.out.println("User created: " + createdUser);
        return "redirect:/login";
    }

    @GetMapping("/admin/register")
    public String adminGetRegister(WebRequest request, Model model) {
        var user = new User();
        user.setPassword("Password-#1");
        model.addAttribute("user", user);
        return "admin-register";
    }

    @PostMapping("/admin/register")
    public String adminPostRegister(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               Errors errors) throws AppException {

        redirectAttributes.addFlashAttribute("message", "Failed username");
        redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        if (result.hasErrors() || userService.existsByEmail(user.getEmail())) {
            System.out.println(result.getAllErrors() + "\n" + userService.existsByEmail(user.getEmail()));
            return "admin-register";
        }
        redirectAttributes.addFlashAttribute("message", "Success");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        user.setAddress("Enter address");
        user.setMobile("Enter mobile number");
        var createdUser = userService.create(user);
        return "redirect:/users/list";
    }

}

