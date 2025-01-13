package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Component
public class AddVariant {
    private String articleCode;
    private Double price;
    private String colorCode;
    private String color;
    private Integer quantity;
    private String[] size;
    private List<MultipartFile> images;
    private Integer maxQuantity;
}
