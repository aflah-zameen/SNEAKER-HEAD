package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ToString
public class AddressDto {
    private Long id;
    private WebUser user;

    @NotBlank(message = "Street address is required")
    private String street;

    @NotBlank(message = "Building is required")
    private String building;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Landmark is required")
    private String landmark;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Phone number is required")
    private Long phone;

    @NotBlank(message = "Country is required")
    private String country;

    private String instructions;
    private boolean defaultAddress;

    @NotBlank(message = "Type is required")
    private String type;
}
