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
    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name is required")
    private String name;

    @Email(message = "Enter a valid email address")
    @NotNull(message = "Email is required")
    @NotEmpty(message = "Email is required")
    private String email;


    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    @NotNull(message = "Password is required")
    @NotEmpty(message = "Password is required")
    private String password;


    @NotNull(message = "Confirm Password is required")
    @NotEmpty(message = "Confirm Password is required")
    private String confirmPassword;

    private String address;

    private String joinDate;

    private Enums.Roles role;

    @Column(name = "pin")
    @Size(min = 11, max = 11, message = "Personal Identification Number must be 11 digits long")
    @Pattern(regexp = "^[0-9]*$", message = "Personal Identification Number must contain only digits")
    @NotNull(message = "Personal Identification Number is required")
    private String personalIdentificationNumber;


    @Size(min = 9, message = "Mobile number must be at least 9 characters long")
    @Pattern(regexp = "^[0-9]*$", message = "Mobile number must contain only digits")
    private String mobile;

    private String jobTitle;

    private String captcha;

    private String hiddenCaptcha;

    private String realCaptcha;

}
