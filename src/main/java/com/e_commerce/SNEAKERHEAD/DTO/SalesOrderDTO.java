package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Setter
@Getter
@ToString
public class SalesOrderDTO {
    private Long id;
    private String userName;
    private UserAddress address;
    private String paymentMethod;
    private Double orderTotal;
    private Double deductedAmount;
    private LocalDate orderDate;
    private String orderStatus;
}
