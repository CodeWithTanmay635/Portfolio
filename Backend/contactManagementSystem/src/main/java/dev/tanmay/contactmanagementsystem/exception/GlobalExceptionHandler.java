package dev.tanmay.contactmanagementsystem.exception;

import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{
    //ExceptionHandler method makes clean JSON
  @ExceptionHandler(AlreadyRepliedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleAlreadyReplied(AlreadyRepliedException ex){
      log.warn(ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(DuplicateContactException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleDuplicateContact(DuplicateContactException ex){
      log.warn(ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(ContactNotFoundException ex){
      log.warn(ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(InvalidReplyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidReply(InvalidReplyException ex){
      log.warn(ex.getMessage());
  }
}
