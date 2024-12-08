package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.Cart;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
@Component
public class OrderDto {
    private Long id;
    private Long variantId;
    private Long addressId;
    private Double deliveryCharge;
    private Double orderTotal;
    private String paymentMethod;
    private Integer quantity;
    private String selectedSize;
}
