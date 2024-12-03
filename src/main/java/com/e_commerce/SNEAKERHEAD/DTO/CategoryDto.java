package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
