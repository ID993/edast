package com.ivodam.finalpaper.edast.controller;

import cn.apiclub.captcha.Captcha;
import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.UserService;
import com.ivodam.finalpaper.edast.utility.CaptchaUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
@AllArgsConstructor
public class Register {

    private UserService userService;

    @GetMapping("/register")
    public String getRegister(Model model) {
        var user = new UserDto();
        getCaptcha(user);
        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model) {
        var message = userService.isUserLegit(user);
        if (result.hasErrors()) {
            getCaptcha(user);
            return "register";
        }
        else if (!message.equals("Success")) {
            getCaptcha(user);
            model.addAttribute("message", message);
            model.addAttribute("user", user);
            return "/register";
        }
        else {
            user.setRole(Enums.Roles.ROLE_USER);
            userService.create(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/admin/register")
    public String adminGetRegister(WebRequest request, Model model) {
        var user = new User();
        user.setPassword("Password#11");
        model.addAttribute("user", user);
        model.addAttribute("jobs", Enums.JobPosition.values());
        return "admin/admin-register";
    }

    @PostMapping("/admin/register")
    public String adminPostRegister(@Valid @ModelAttribute("user") User user,
                                    BindingResult result,
                                    @RequestParam String job,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("jobs", Enums.JobPosition.values());
            return "admin/admin-register";
        } else if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("jobs", Enums.JobPosition.values());
            model.addAttribute("message", "Email already exists!");
            return "admin/admin-register";
        }
        user.setJobTitle(job);
        var savedUser = userService.create(user);
        return "redirect:/users?type=" + savedUser.getRole().getDisplayName();
    }

    private void getCaptcha(UserDto user) {
        Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
        user.setHiddenCaptcha(captcha.getAnswer());
        user.setCaptcha("");
        user.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));
    }

}

