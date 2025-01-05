package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.DTO.ProductDto;
import com.e_commerce.SNEAKERHEAD.DTO.ProductVariantDTO;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingHelper.class}) // Reference the helper class
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "productVariantDTOs", source = "productVariants"), // Map productVariants list
            @Mapping(target = "defaultVariantDTO", expression = "java(getDefaultVariant(product.getProductVariants()))"), // Custom mapping for defaultVariant
            @Mapping(target = "brandName", source = "brand.name"), // Map Brand's name to brandName in DTO
            @Mapping(target = "categoryName", source = "category.name"), // Map Category's name to categoryName in DTO
            @Mapping(target = "appliedOffer", source = "appliedOffer")
    })
    ProductDto toDTO(Product product);

    @Mappings({
            @Mapping(target = "productVariants", source = "productVariantDTOs"), // Map productVariantDTOs to productVariants
            @Mapping(target = "brand", source = "brandName", qualifiedByName = "brandNameToBrand"), // Use helper for brand mapping
            @Mapping(target = "category", source = "categoryName", qualifiedByName = "categoryNameToCategory") // Use helper for category mapping
    })
    Product toEntity(ProductDto productDto);

    // List mappings
    List<ProductDto> toDTOList(List<Product> products);
    List<Product> toEntityList(List<ProductDto> productDtos);

    // Mapping for ProductVariant to ProductVariantDTO
    ProductVariantDTO toProductVariantDTO(ProductVariant productVariant);

    // Mapping for List<ProductVariant> to List<ProductVariantDTO>
    List<ProductVariantDTO> toProductVariantDTOList(List<ProductVariant> productVariants);

    // Default variant mapping logic
    default ProductVariantDTO getDefaultVariant(List<ProductVariant> productVariants) {
        if (productVariants != null && !productVariants.isEmpty()) {
            return toProductVariantDTO(productVariants.get(0)); // Assuming the first variant is default
        }
        return null; // Return null if no variants exist
    }
}

