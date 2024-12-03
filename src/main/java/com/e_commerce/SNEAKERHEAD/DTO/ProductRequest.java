package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
public class ProductRequest {
    private String name;
    private String description;
    private String color;
    private String brand;
    private String category;
    private Double price;
    private Integer quantity;
    private List<String> images = new ArrayList<>();
    private Double size;
}



