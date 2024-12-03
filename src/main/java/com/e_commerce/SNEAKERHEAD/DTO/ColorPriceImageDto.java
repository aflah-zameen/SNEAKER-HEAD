package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;

@Component
@Getter
@Setter
@ToString
public class ColorPriceImageDto {
    private String color;
    private List<String> images;
    private String price;
    private List<String> size;
    private String articleCode;
}
