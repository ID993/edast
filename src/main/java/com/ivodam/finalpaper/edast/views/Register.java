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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ivodam.finalpaper.edast.utility.CaptchaUtil;

import cn.apiclub.captcha.Captcha;

@Controller
@AllArgsConstructor
public class Register {

    //implement password confirmation and userdto

    private UserService userService;

    @GetMapping("/register")
    public String getRegister(WebRequest request, Model model) {
        var user = new UserDto();
        getCaptcha(user);
        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute("user") UserDto user,
                                   Model model,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   Errors errors) throws AppException {
        var message = userService.isUserLegit(user);
        if (message.equals("Success")) {
            user.setRole(Enums.Roles.ROLE_USER);
            userService.create(user);
            model.addAttribute("message", "User Registered successfully!");
            return "redirect:/login";
        }
        else {
            model.addAttribute("message", message);
            getCaptcha(user);
            model.addAttribute("user", user);
        }
        return "register";
//        redirectAttributes.addFlashAttribute("message", "Failed username");
//        redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
//        if (result.hasErrors() || userService.existsByEmail(user.getEmail())) {
//            return "register";
//        }
//        redirectAttributes.addFlashAttribute("message", "Success");
//        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
//        if(user.getCaptcha().equals(user.getHiddenCaptcha())) {
//            user.setRole(Enums.Roles.ROLE_USER);
//            userService.create(user);
//            model.addAttribute("message", "User Registered successfully!");
//            return "redirect:/login";
//        }
//        else {
//            model.addAttribute("message", "Invalid Captcha");
//            getCaptcha(user);
//            model.addAttribute("user", user);
//        }
//        user.setRole(Enums.Roles.ROLE_USER);
//        userService.create(user);
//        return "register";
    }

    @GetMapping("/admin/register")
    public String adminGetRegister(WebRequest request, Model model) {
        var user = new UserDto();
        user.setPassword("Password-#1");
        user.setConfirmPassword("Password-#1");
        model.addAttribute("user", user);
        model.addAttribute("jobs", Enums.JobPosition.values());
        return "admin/admin-register";
    }

    @PostMapping("/admin/register")
    public String adminPostRegister(@Valid @ModelAttribute("user") UserDto user,
                                    @RequestParam String job,
                                    BindingResult result,
                                    //RedirectAttributes redirectAttributes,
                                    //HttpServletRequest request,
                                    Errors errors) {

        //redirectAttributes.addFlashAttribute("message", "Failed username");
        //redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        if (result.hasErrors()) {  //|| userService.existsByEmail(user.getEmail())
            return "admin/admin-register";
        }
        //redirectAttributes.addFlashAttribute("message", "Success");
        //redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        user.setAddress("Enter address");
        user.setMobile("Enter mobile number");
        user.setJobTitle(job);
        userService.create(user);
        return "redirect:/users/all";
    }

    private void getCaptcha(UserDto user) {
        Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
        user.setHiddenCaptcha(captcha.getAnswer());
        user.setCaptcha(""); // value entered by the User
        user.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));

    }

}

