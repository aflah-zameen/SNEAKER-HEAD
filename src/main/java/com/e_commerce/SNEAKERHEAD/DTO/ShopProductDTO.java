package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.VariantCard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductDTO {
    private Long id;
    private String name;
    private String brand;
    private Boolean status;
    private Boolean wishListStatus=false;
    private String offerName=null;
    private Integer discountPercentage=null;
    private List<VariantCard> variantCards;
    private VariantCard defaultVariant;
}
