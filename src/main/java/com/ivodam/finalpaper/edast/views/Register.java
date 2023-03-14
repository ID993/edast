package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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


    private UserService userService;

    private static final String registerUrl = "register";

    @GetMapping("/register")
    public String getRegister(WebRequest request, Model model) {
        var user = new User();
        model.addAttribute("user", user);
        return registerUrl;
    }


    @PostMapping("/register")
    public String postRegister(@ModelAttribute("user") User user,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   Errors errors) {

        redirectAttributes.addFlashAttribute("message", "Failed username");
        redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        if (result.hasErrors() || userService.findByEmail(user.getEmail()).isPresent()) {
            return registerUrl;
        }
        redirectAttributes.addFlashAttribute("message", "Success");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        user.setRole("ROLE_USER");
        userService.create(user);
        return "/login";
    }
}

