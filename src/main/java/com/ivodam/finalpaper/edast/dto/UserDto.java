package com.ivodam.finalpaper.edast.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivodam.finalpaper.edast.enums.Enums;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    private String name;

    @Email(message = "Enter a valid email address")
    @NotNull(message = "Email is required")
    private String email;


    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    @NotNull(message = "Password is required")
    private String password;


    @Size(min = 8, message = "Confirm Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Confirm Password must contain at least one uppercase letter, one lowercase letter and one number")
    @NotNull(message = "Confirm Password is required")
    private String confirmPassword;


    @Size(min = 2, message = "Address must be at least 8 characters long")
    private String address;


    private String joinDate;


    private Enums.Roles role;


    @Column(name = "pin")
    @Size(min = 11, max = 11, message = "Personal Identification Number must be 11 digits long")
    @NotNull(message = "Personal Identification Number is required")
    private String personalIdentificationNumber;


    @Size(min = 9, message = "Mobile number must be at least 9 characters long")
    private String mobile;


    private String jobTitle;


    private String captcha;


    private String hiddenCaptcha;

    private String realCaptcha;

}
