package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ToString
public class EditOrder {
    private Long orderId;
    private String orderStatus;
    private String paymentMethod;
    private String userName;
}
