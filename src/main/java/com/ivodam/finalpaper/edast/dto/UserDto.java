package com.ivodam.finalpaper.edast.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

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

    @JsonProperty("profession")
    private String profession;

    @JsonProperty("address")
    private String address;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("numberOfIdCard")
    private String numberOfIdCard;

    @JsonProperty("purposeOfResearch")
    private String purposeOfResearch;

    @JsonProperty("joinDate")
    private LocalDate joinDate;

    @JsonProperty("role")
    private String role;
}
