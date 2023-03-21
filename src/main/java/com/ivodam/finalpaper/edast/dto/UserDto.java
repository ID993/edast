package com.ivodam.finalpaper.edast.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivodam.finalpaper.edast.enums.Enums;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("confirmPassword")
    private String confirmPassword;


    @JsonProperty("address")
    private String address;

    @JsonProperty("joinDate")
    private String joinDate;

    @JsonProperty("role")
    private Enums.Roles role;

    @JsonProperty("pin")
    private String personalIdentificationNumber;
    @JsonProperty("mobile")
    private String mobile;

//    @JsonProperty("authorities")
//    private Set<GrantedAuthority> authorities;



}
