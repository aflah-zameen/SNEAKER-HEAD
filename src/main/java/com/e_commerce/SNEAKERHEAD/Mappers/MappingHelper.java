package com.e_commerce.SNEAKERHEAD.Mappers;

import com.e_commerce.SNEAKERHEAD.Entity.Brand;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.BrandRepository;
import com.e_commerce.SNEAKERHEAD.Repository.CategoryRepository;
import com.e_commerce.SNEAKERHEAD.Repository.ProductVariantRepository;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MappingHelper {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Named("brandNameToBrand")
    public Brand brandNameToBrand(String brandName) {
        if (brandName == null || brandName.isEmpty()) {
            return null;
        }
        return brandRepository.findByName(brandName)
                .orElseThrow(() -> new RuntimeException("Brand not found with name: " + brandName));
    }

    @Named("categoryNameToCategory")
    public Category categoryNameToCategory(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return null;
        }
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + categoryName));
    }

    @Named("productVariantIdToProductVariant")
    public ProductVariant productVariantIdToProductVariant(Long variantId)
    {
        if (variantId == null ) {
            return null;
        }
        return productVariantRepository.findById(variantId).orElseThrow(() -> new NullPointerException());
    }

    @Named("userIdToUser")
    public WebUser userIdToUser(Long userId)
    {
        if (userId == null ) {
            return null;
        }
        return userRepository.findById(userId).orElseThrow(() -> new NullPointerException());
    }
}
