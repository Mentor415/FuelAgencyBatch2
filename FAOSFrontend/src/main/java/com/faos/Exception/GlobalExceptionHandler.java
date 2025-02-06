package com.faos.Exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Handle Page Not Found Exception (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handlePageNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "Page Not Found");
        model.addAttribute("message", "The page you are looking for does not exist.");
        return "404";
    }

    // Handle Illegal Argument Exception (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        model.addAttribute("error", "Invalid Argument");
        model.addAttribute("message", "The provided argument is invalid.");
        return "error";
    }

    // Handle Entity Not Found Exception (404)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleEntityNotFoundException(NoSuchElementException ex, Model model) {
        model.addAttribute("error", "Entity Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    // Handle Null Pointer Exception (400)
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex, Model model) {
        model.addAttribute("error", "Unexpected Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        return "error";
    }

    // Handle Validation Errors (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex, Model model) {
        model.addAttribute("error", "Validation Failed");
        model.addAttribute("message", "Request contains invalid data.");
        return "error";
    }

    // Handle Generic Exceptions (500)
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        return "error";
    }

}
