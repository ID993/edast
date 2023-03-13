package com.ivodam.finalpaper.edast.exceptions;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppException extends Exception {

    private final HttpStatus status;

    public AppException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

}
