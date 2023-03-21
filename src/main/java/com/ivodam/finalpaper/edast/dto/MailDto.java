package com.ivodam.finalpaper.edast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {
    private String from;

    private String to;

    private String subject;

    private String message;
}
