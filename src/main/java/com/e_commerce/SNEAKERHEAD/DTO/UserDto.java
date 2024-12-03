package com.e_commerce.SNEAKERHEAD.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Getter
@Setter
@ToString
public class UserDto {
    private Long id;

    @NotBlank(message = "Name cannot be blank.")
    private String name;
    @NotBlank(message = "Email cannot be blank.")
    private String email;
    @NotNull(message = "Phone cannot be blank.")
    private Long phone;
    private LocalDate joinDate;
    private String gender;
    private String Status;
    private AddressDto defaultAddress;
}
