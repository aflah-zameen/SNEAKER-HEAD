package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private Long phone;
    private LocalDate joinDate;
    private String gender;
    private boolean status;
    private AddressDTO defaultAddress;
    private UserRole role;
}
