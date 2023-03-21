package com.ivodam.finalpaper.edast.exceptions;

import java.util.Date;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class ControllerExceptionHandler {

        @ExceptionHandler(value = {AppException.class})
        @ResponseBody
        public ResponseEntity<ErrorMessages> appException(AppException ex, WebRequest request) {
            return ResponseEntity
                    .status(ex.getStatus())
                    .body(ErrorMessages.builder()
                            .status(ex.getStatus().value())
                            .timestamp(new Date())
                            .message(ex.getMessage())
                            .description(request.getDescription(false)).build());

        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorMessages> globalExceptionHandler(Exception ex, WebRequest request) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorMessages.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .timestamp(new Date())
                            .message(ex.getMessage())
                            .description(request.getDescription(false)).build());
        }


        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorMessages> badRequestExceptionHandler(Exception ex, WebRequest request) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessages.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .timestamp(new Date())
                            .message(ex.getMessage())
                            .description(request.getDescription(false)).build());
        }

}
