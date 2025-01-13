package com.e_commerce.SNEAKERHEAD.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class ResetPasswordDTO {
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotBlank(message="Password is required")
    @Length(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[@#$%^&+=!].*", message = "Password must contain at least one special character (@#$%^&+=!)")
    private String password;
}
