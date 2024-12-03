package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
public class AddProduct {
    List<String> brands;
    List<String> categories;
}
