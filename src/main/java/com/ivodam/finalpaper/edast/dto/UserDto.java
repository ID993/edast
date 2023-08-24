package com.ivodam.finalpaper.edast.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ivodam.finalpaper.edast.enums.Enums;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {


    private UUID id;

    @Size(min = 2, message = "Name must be at least 2 characters long")
//    @NotNull(message = "Name is required")
//    @NotEmpty(message = "Name is required")
    private String name;

    @Email(message = "Enter a valid email address")
    @NotEmpty(message = "Email is required")
    private String email;


    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
//    @NotEmpty(message = "Password is required")
    private String password;


    @NotEmpty(message = "Confirm Password is required")
    private String confirmPassword;

    private String joinDate;

    private Enums.Roles role;

    private String jobTitle;

    private String captcha;

    private String hiddenCaptcha;

    private String realCaptcha;

}
