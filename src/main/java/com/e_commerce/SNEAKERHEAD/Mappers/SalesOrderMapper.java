package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.DTO.SalesOrderDTO;
import com.e_commerce.SNEAKERHEAD.Entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel ="spring" )
public interface SalesOrderMapper {

    OrderEntity toEntity(SalesOrderDTO salesOrderDTO);
    @Mappings({
            @Mapping(target = "userName" , source = "user.fullName"),
            @Mapping(target = "orderStatus" , source = "status"),
            @Mapping(target = "deductedAmount", source = "deductedAmount")
    })
    SalesOrderDTO toDTO(OrderEntity order);

    List<SalesOrderDTO> toDTOList(List<OrderEntity> orderList);

}
