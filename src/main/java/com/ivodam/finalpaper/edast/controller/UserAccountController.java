package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.service.UserService;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class UserAccountController {

  private final UserService userService;
  private final PasswordHandler passwordHandler;
  private final UserMapper userMapper;

  @GetMapping("/account")
  public String getAccount(Authentication authentication, Model model)
      throws AppException {

    var user = userService.findByEmail(authentication.getName());
    model.addAttribute("user", user);
    return "account/account";
  }

  @GetMapping("/account/edit")
  public String editUser(Authentication authentication, Model model)
      throws AppException {

    var user = userMapper.userToUserDto(
        userService.findByEmail(authentication.getName()));

    model.addAttribute("user", user);
    return "account/account-edit";
  }

  @PostMapping("/account/edit")
  public String updateUser(Authentication authentication,
                           @ModelAttribute("user") UserDto userDto)
      throws AppException {

    userService.updateOwnAccount(authentication.getName(), userDto);
    return "redirect:/account";
  }

  @PostMapping("/account/delete")
  public String deleteCurrentUser(Authentication authentication,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
      throws AppException {

    var user = userService.findByEmail(authentication.getName());
    userService.deleteById(user.getId());

    new SecurityContextLogoutHandler().logout(request, response,
                                              authentication);

    return "redirect:/";
  }

  @GetMapping("/account/change-password")
  public String changePassword() {
    return "password/change-password";
  }

  @PostMapping("/account/change-password")
  public String postChangePassword(Authentication authentication,
                                   @RequestParam String oldPassword,
                                   @RequestParam String password,
                                   @RequestParam String confirmPassword,
                                   Model model)
      throws AppException, IOException {

    var user = userService.findByEmail(authentication.getName());

    if (!passwordHandler.checkIfValidOldPassword(user, oldPassword)) {
      model.addAttribute("message", "Wrong old password");
      return "password/change-password";
    }

    if (!password.equals(confirmPassword)) {
      model.addAttribute("message", "Passwords do not match");
      return "password/change-password";
    }

    if (!passwordHandler.isValid(password)) {
      model.addAttribute("message",
                         "Password must contain at least 8 characters, "
                             + "one uppercase letter, one lowercase letter, "
                             + "one number and one special character");
      return "password/change-password";
    }

    userService.updatePassword(user, password);
    return "redirect:/account";
  }
}