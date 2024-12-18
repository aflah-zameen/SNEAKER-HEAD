package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;


@Component
@Setter
@Getter
@Data
@ToString
public class ProductVariantDTO {
   private Long id;
   private String articleCode;
   private Double price;
   private String color;
   private Integer quantity;
   private List<String> size;
   private List<String> images;
   private String formattedPrice;
   private Integer maxQuantity;
   public String FormattedPrice(){
      DecimalFormat formatter = new DecimalFormat("#,##0.00");
      return formatter.format(price);
   }
}
