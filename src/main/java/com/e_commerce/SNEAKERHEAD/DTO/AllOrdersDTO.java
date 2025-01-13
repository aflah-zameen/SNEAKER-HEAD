package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
public class AllOrdersDTO {
    private List<OrderEntityDTO> orders;
    private Integer totalPages;
}
