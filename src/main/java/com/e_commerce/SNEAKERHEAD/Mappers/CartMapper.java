package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.DTO.CartDTO;
import com.e_commerce.SNEAKERHEAD.DTO.ProductDto;
import com.e_commerce.SNEAKERHEAD.Entity.Cart;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingHelper.class})
public interface CartMapper {
    @Mappings({
            @Mapping(target = "userId", source = "user.id"), // Map Brand's name to brandName in DTO
            @Mapping(target = "productVariantId", source = "productVariant.id") // Map Category's name to categoryName in DTO
    })
    CartDTO toDTO(Cart cart);

    @Mappings({
            @Mapping(target = "productVariant", source = "productVariantId", qualifiedByName = "productVariantIdToProductVariant"), // Use helper for brand mapping
            @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser") // Use helper for category mapping
    })
    Cart toEntity(CartDTO cartDTO);

    // List mappings
    List<CartDTO> toDTOList(List<Cart> carts);
    List<Cart> toEntityList(List<CartDTO> cartDTOs);

}
