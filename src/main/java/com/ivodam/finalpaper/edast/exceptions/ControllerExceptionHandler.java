package com.ivodam.finalpaper.edast.exceptions;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ModelAndView handleAppException(AppException ex) {
    return createErrorView(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessages>
  handleValidationException(MethodArgumentNotValidException ex,
                            WebRequest request) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorMessages.builder()
                  .status(HttpStatus.BAD_REQUEST.value())
                  .timestamp(new Date())
                  .message("Validation failed")
                  .description(request.getDescription(false))
                  .build());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ModelAndView handleNotFound(NoResourceFoundException ex) {
    return createErrorView(HttpStatus.NOT_FOUND, "Not found");
  }

  private ModelAndView createErrorView(HttpStatus status, String message) {
    var modelAndView = new ModelAndView("error");
    modelAndView.setStatus(status);
    modelAndView.addObject("errorMessage", message);
    modelAndView.addObject("errorStatus", status.value());
    return modelAndView;
  }
}