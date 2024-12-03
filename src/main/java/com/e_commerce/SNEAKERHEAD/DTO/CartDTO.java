package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
@Setter
@Getter
@ToString
public class CartDTO {
    private Long id;
    private Long userId;
    private Long productVariantId;
    private Integer quantity;
    private Double totalAmount;
    private Double size;
    private String formattedTotalAmount;

    public String getFormattedPrice(){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(totalAmount);
    }
}
