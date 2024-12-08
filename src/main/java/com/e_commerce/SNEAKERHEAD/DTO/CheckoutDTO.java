package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CheckoutDTO {
    private Long varinatId;
    private String selectedSize;
    private Integer selectedQuantity;
    private Double price;
}
