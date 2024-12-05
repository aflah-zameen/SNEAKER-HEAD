package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@Data
public class UsersList {
    private Long id;
    private String name;
    private String email;
    private String date;
    private Long phone;
    private Boolean status;
}
