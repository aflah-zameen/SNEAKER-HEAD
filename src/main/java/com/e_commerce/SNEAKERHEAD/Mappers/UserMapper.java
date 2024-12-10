package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.DTO.UserDTO;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "fullName", target = "fullName")
    WebUser toEntity(UserDTO userDTO);
    @Mapping(source = "fullName", target = "fullName")
    UserDTO toDTO(WebUser webUser);
}
