package com.ivodam.finalpaper.edast.helpers;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.security.SecurityConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class PasswordHandler {

    private SecurityConfiguration configuration;
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$";
            //"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean checkIfValidOldPassword(User user, String oldPassword){
        return configuration.passwordEncoder().matches(oldPassword, user.getPassword());
    }

    public boolean checkConfirmPassword(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }




}
