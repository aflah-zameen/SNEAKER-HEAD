package com.e_commerce.SNEAKERHEAD.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VariantCard {
    private String colorCode;
    private String color;
    private Integer quantity;
    private String coverImageURL;
    private Double price;
    private Double offerPrice;
    private String status;
}
