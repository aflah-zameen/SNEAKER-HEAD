package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingDTO {
    private String name;
    private Long totalSales;
}
