package com.ivodam.finalpaper.edast.utility;

import com.ivodam.finalpaper.edast.entity.User;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PasswordHandler {

  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

  private final PasswordEncoder passwordEncoder;

  public boolean isValid(String password) {
    return password != null && PASSWORD_PATTERN.matcher(password).matches();
  }

  public boolean checkIfValidOldPassword(User user, String oldPassword) {
    return user != null && oldPassword != null && user.getPassword() != null &&
        passwordEncoder.matches(oldPassword, user.getPassword());
  }

  public boolean checkConfirmPassword(String password, String confirmPassword) {
    return password != null && password.equals(confirmPassword);
  }
}