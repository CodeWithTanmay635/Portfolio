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
      log.warn("Already Replied : {}",ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(DuplicateContactException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDuplicateContact(DuplicateContactException ex){
      log.warn("Duplicate found : {}",ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleContactNotFound(ContactNotFoundException ex){
      log.warn("Contact Not Found : {}",ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(InvalidReplyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidReply(InvalidReplyException ex){
      log.warn("Invalid Reply : {}",ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

  @ExceptionHandler(InvalidStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidStatus(InvalidStatusTransitionException ex){
      log.warn("Invalid Status : {}",ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

 @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleMessageNotFound(MessageNotFoundException ex){
      log.warn("Message Not found : {}",ex.getMessage());
      return ApiResponse.error(ex.getMessage());
  }

 @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ApiResponse.error(ex.getMessage());
  }
}
