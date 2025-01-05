package com.e_commerce.SNEAKERHEAD.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Getter
@Setter
@ToString
public class OfferDTO {
    private  Long id;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Offer name cannot be blank")
    private String offerName;
    @NotNull(message = "Discount value cannot be blank")
    private Integer discountValue;
    @NotBlank(message = "Please select a offer type")
    private String type;
    @NotNull(message = "Select a item from the list")
    private Long typeId;
    @NotNull(message = "Please mention expiry date")
    private LocalDate endDate;

    private String appliedOn;

    private String categoryName;
    private String productName;
}
