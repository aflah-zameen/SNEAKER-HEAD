package com.e_commerce.SNEAKERHEAD.DTO;

import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntityDTO {
    private Long id;
    private String userName;
    private AddressDTO address;
    private Double deductedAmount;
    private Double orderTotal;
    private LocalDate orderDate;
    private String paymentMethod;
    private String orderStatus;
    private boolean cancellation;

}
