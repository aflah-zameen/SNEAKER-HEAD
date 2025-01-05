package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.TopSellingDTO;
import com.e_commerce.SNEAKERHEAD.Repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    SaleRepository saleRepository;


    public List<TopSellingDTO> getTopSelling(String type)
    {
        List<TopSellingDTO> topSellingDTOS = new ArrayList<>();
        List<Object[]> list = new ArrayList<>();
        if(type.equalsIgnoreCase("product"))
        {
             list = saleRepository.getTopSellingProducts();
        }
        else if(type.equalsIgnoreCase("brand"))
        {
            list = saleRepository.getTopSellingBrands();
        }
        else
        {
            list = saleRepository.getTopSellingCategories();
        }
        return list.stream().limit(10).map(li -> new TopSellingDTO((String)li[0],(Long)li[1])).toList();
    }
}
