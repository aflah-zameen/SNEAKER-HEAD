package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class OfferDTO {
    private  Long id;
    private String description;
    private String offerName;
    private Integer discountValue;
    private String offerType;
    private Long typeId;

}
