package com.faos.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handlePageNotFound(NoHandlerFoundException ex) {
        return new ResponseEntity<>("Page Not Found ! The page you are looking for does not exist.", HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDatabaseException(DataAccessException ex) {
        return new ResponseEntity<>("Database Error! A database error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(NoSuchElementException ex) {
    	Map<String, String> error = new HashMap<>();
    	error.put("error", "Entity Not Found");
    	error.put("message", ex.getMessage());
    	return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity<>("Null Pointer Exception ! An unexpected error occurred. Please try again later.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>("Invalid Argument", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Internal Server Error ! An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
