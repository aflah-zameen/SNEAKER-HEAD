package com.e_commerce.SNEAKERHEAD.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Getter
@Setter
public class SalesPaginationDTO {
    private Integer pageNumber=0;
    private Integer pageSize=5;
    private String sortBy="id";
    private String order="ASC";

    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;

    private Double overallSales;
    private Long overallOrders;
    private Double discountValue;

    List<SalesOrderDTO> ordersList;
    private Integer totalPages;

    public Pageable getPageablePage()
    {
        return PageRequest.of(pageNumber,pageSize, Sort.Direction.fromString(order),sortBy);
    }
}
