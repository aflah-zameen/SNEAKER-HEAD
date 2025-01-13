package com.e_commerce.SNEAKERHEAD.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class ChangePassword {

    @NotBlank(message = "Current password cannot be blank")
    private String currentPassword;
    @NotBlank(message="New password is required")
    @Length(min = 8, max = 20, message = "New password must be between 8 and 20 characters")
    @Pattern(regexp = ".*\\d.*", message = "New password must contain at least one digit")
    @Pattern(regexp = ".*[@#$%^&+=!].*", message = "New password must contain at least one special character (@#$%^&+=!)")
    private String newPassword;
}
