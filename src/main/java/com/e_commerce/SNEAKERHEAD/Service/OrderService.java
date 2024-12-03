package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.Cart;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import com.e_commerce.SNEAKERHEAD.Repository.CartRepository;
import com.e_commerce.SNEAKERHEAD.Repository.OrderItemsRepository;
import com.e_commerce.SNEAKERHEAD.Repository.OrderRepository;
import com.e_commerce.SNEAKERHEAD.Repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemsRepository orderItemsRepository;

    @Autowired
    ProductVariantRepository productVariantRepository;

    @Transactional
    public void updateCartAndStock(List<Cart> carts, Long userId)
    {
        for(Cart cart :carts)
        {
            ProductVariant productVariant = cart.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity()-cart.getQuantity());
            if(productVariant.getQuantity()<=0)
            {
                productVariant.setStockStatus("UNAVAILABLE");
            }
            productVariantRepository.save(productVariant);
        }
        cartRepository.deleteByUser_id(userId);
    }
}
