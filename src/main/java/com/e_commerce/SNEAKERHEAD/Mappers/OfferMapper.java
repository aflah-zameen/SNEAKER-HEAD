package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.DTO.OfferDTO;
import com.e_commerce.SNEAKERHEAD.Entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferMapper {
    Offer toEntity(OfferDTO offerDTO);

    OfferDTO toDTO(Offer offer);

    List<OfferDTO> toDTOList(List<Offer> offers);
}
