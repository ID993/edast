package com.ivodam.finalpaper.edast.controller;

import cn.apiclub.captcha.Captcha;
import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.UserService;
import com.ivodam.finalpaper.edast.utility.CaptchaUtil;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                             BindingResult result, Model model) {

    if (result.hasErrors()) {
      getCaptcha(user);
      return "register";
    }

    var message = userService.isUserLegit(user);

    if (!"Success".equals(message)) {
      getCaptcha(user);
      model.addAttribute("message", message);
      return "register";
    }

    userService.registerUser(user);
    return "redirect:/login";
  }

  @GetMapping("/admin/register")
  public String adminGetRegister(Model model) {
    model.addAttribute("user", new UserDto());
    addAdminRegistrationOptions(model);
    return "admin/admin-register";
  }

  @PostMapping("/admin/register")
  public String adminPostRegister(@Valid @ModelAttribute("user") UserDto user,
                                  BindingResult result,
                                  @RequestParam String job, Model model)
      throws AppException {

    if (!Objects.equals(user.getPassword(), user.getConfirmPassword())) {
      result.rejectValue("confirmPassword", "password.mismatch",
                         "Passwords do not match");
    }

    if (user.getRole() == null ||
        user.getRole() != Enums.Roles.ROLE_ADMIN &&
            user.getRole() != Enums.Roles.ROLE_EMPLOYEE) {
      result.rejectValue("role", "role.invalid", "Select a valid staff role");
    }

    if (!result.hasFieldErrors("email") &&
        userService.existsByEmail(user.getEmail())) {
      result.rejectValue("email", "email.exists", "Email already exists");
    }

    if (result.hasErrors()) {
      addAdminRegistrationOptions(model);
      return "admin/admin-register";
    }

    var savedUser = userService.createStaffUser(user, user.getRole(), job);

    return "redirect:/users?type=" + savedUser.getRole().getDisplayName();
  }

  private void addAdminRegistrationOptions(Model model) {
    model.addAttribute("jobs", Enums.JobPosition.values());
  }

  private void getCaptcha(UserDto user) {
    Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
    user.setHiddenCaptcha(captcha.getAnswer());
    user.setCaptcha("");
    user.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));
  }
}
