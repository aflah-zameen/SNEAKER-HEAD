package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Setter
@Getter
public class SortProductDTO {
    private String sortBy="id";
    private String order="ASC";
    private Integer pageNumber =0;
    private Integer size=2;

    public Pageable  getPageablePage()
    {
        return PageRequest.of(pageNumber,size,Sort.Direction.fromString(order),sortBy);
    }

}
