package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.ProductDto;
import com.e_commerce.SNEAKERHEAD.DTO.ProductVariantDTO;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import com.e_commerce.SNEAKERHEAD.Enums.StockStatus;
import com.e_commerce.SNEAKERHEAD.Enums.UserRole;
import com.e_commerce.SNEAKERHEAD.Enums.UserStatus;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminManagementService {


    BCryptPasswordEncoder passwordEncoder;
    public AdminManagementService()
    {
        this.passwordEncoder =new BCryptPasswordEncoder();
    }




    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariantRepository productVariantRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional
    public Product addProduct(ProductDto productDto, Brand brand, Category category)
    {
        Product product = new Product();
        System.out.println(">>>>>>>>"+productDto.getManufacturer()+">>>>>>>>>>>>>>>");
        String name = productDto.getName().trim();
            product.setName(name);
//            product.setCategory( categoryRepository.findByName(productDto.getCategory().getName()).orElseThrow(()-> new NullPointerException("Category not found")));
//            product.setBrand(brandRepository.findByName(productDto.getBrand().getName()).orElseThrow(()-> new NullPointerException("Brand not Found")));
            product.setDescription(productDto.getDescription());
            product.setCountryOfOrigin(productDto.getCountryOfOrigin());
            product.setGenericName(productDto.getGenericName());
            product.setImportedBy(productDto.getImportedBy());
            product.setManufacturer(productDto.getManufacturer());
            product.setMarketedBy(productDto.getMarketedBy());
            product.setWeight(productDto.getWeight());
            product.setStatus(true);
            product.setBrand(brand);
            product.setCategory(category);
            return productRepository.save(product);
        }
    @Transactional
    public String addCategory(Category category)
    {
        Optional<Category> temp = categoryRepository.findByName(category.getName());
        if(temp.isPresent())
        {
            return "Already Exist";
        }
        categoryRepository.save(category);
        return "Successfully added";
    }

    @Transactional
    public String addUser(WebUser user)
    {
        Optional<WebUser> temp = userRepository.findByEmail(user.getEmail());
        if(temp.isPresent())
        {
            return "Already Exist";
        }
        user.setJoin_date(LocalDate.now());
        user.setRole(UserRole.USER);
        user.setStatus(true);
        if(user.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
        }
        userRepository.save(user);
        return "Successfully added";
    }


    public void addProductVariant(@Valid ProductVariantDTO productVariantDto, Long productId) {
        Product product = productRepository.findById(productId).orElse(new Product());
        ProductVariant productVariant = new ProductVariant();
        productVariant.setProduct(product);
        productVariant.setColor(productVariantDto.getColor());
        productVariant.setQuantity(productVariantDto.getQuantity());
        productVariant.setSize(productVariantDto.getSize());
        List<String> images= new ArrayList<>();
        for(String image : productVariantDto.getImages())
        {
            images.add("/assests/images/"+image);
        }
        productVariant.setImages(images);
        productVariant.setArticleCode(productVariantDto.getArticleCode());
        productVariant.setPrice(productVariantDto.getPrice());
        productVariant.setStockStatus("AVAILABLE");
        productVariantRepository.save(productVariant);
    }

    public void resetPassword(WebUser user) {
        WebUser userTemp = userRepository.findByEmail(user.getEmail()).orElse(new WebUser());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        userTemp.setPassword(hashedPassword);
        userRepository.save(userTemp);
    }
}
