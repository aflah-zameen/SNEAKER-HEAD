package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.Brand;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.Offer;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Setter
@Getter
@Data
@ToString
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String brandName;
    private String categoryName;
    private String genericName;
    private Double weight;
    private Offer appliedOffer;
    private String manufacturer;
    private String countryOfOrigin;
    private String importedBy;
    private String marketedBy;
    private List<ProductVariantDTO> productVariantDTOs;
    private ProductVariantDTO defaultVariantDTO ;
    private Boolean status;
    private Integer quantity;
    private Boolean wishlisted;
}
