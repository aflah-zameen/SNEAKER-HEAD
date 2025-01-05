package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesOverviewDTO {
    private LocalDate date;
    private Double totalSales;
}
