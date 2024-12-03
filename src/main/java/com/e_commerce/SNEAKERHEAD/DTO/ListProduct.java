package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class ListProduct {
    private Long id;
    private String name;
    private String brand;
    private Integer quantity;
    private String description;
    private String status;
    private ProductVariantDTO productVariantDtos;
    private String defaultColor;
    private String category;
}
