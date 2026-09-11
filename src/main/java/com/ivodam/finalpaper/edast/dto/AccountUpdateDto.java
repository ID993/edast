package com.ivodam.finalpaper.edast.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 255,
        message = "Name must be between 2 and 255 characters")
  private String name;

  @Size(max = 255,
        message = "Occupation or job title must not exceed 255 characters")
  private String jobTitle;
}