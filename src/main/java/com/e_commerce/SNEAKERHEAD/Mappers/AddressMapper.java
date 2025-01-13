package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.DTO.AddressDTO;
import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface AddressMapper {
    AddressDTO toDTO(UserAddress address);
    UserAddress toEntity(AddressDTO addressDTO);
}
