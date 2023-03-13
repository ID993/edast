package com.ivodam.finalpaper.edast.exceptions;

import lombok.*;
import java.util.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessages {

    private int status;
    private Date timestamp;
    private String message;
    private String description;
}
