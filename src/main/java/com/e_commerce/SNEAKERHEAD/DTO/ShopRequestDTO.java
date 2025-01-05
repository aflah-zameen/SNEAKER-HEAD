package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
public class ShopRequestDTO {
    private Integer page=0;
    private Integer size=9;
    private String sortBy="id";
    private String sortDirection="ASC";
    private String searchQuery;
    private Map<String,String> filters;
}
