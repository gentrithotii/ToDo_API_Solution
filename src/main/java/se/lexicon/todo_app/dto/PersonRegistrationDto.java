package se.lexicon.todo_app.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record PersonRegistrationDto(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must be less than 150 characters")
    String email,

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{4,50}$", 
            message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
    //        message = "Password must contain at least one digit, lowercase, uppercase, and special character")
    String password,

    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {
    // Custom validation method
    @AssertTrue(message = "Passwords do not match")
    boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}