package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import com.e_commerce.SNEAKERHEAD.Enums.StockStatus;
import com.e_commerce.SNEAKERHEAD.Enums.UserRole;
import com.e_commerce.SNEAKERHEAD.Enums.UserStatus;
import com.e_commerce.SNEAKERHEAD.Mappers.UserMapper;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Autowired
    UserMapper userMapper;

    @Transactional
    public Product addProduct(ProductDto productDto, Brand brand, Category category)
    {
        Product product = new Product();
        String name = productDto.getName().trim();
            product.setName(name);
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
    public String addUser(UserDTO userDTO, HttpSession session)
    {
        Optional<WebUser> temp = userRepository.findByEmail(userDTO.getEmail());
        String password = (String)session.getAttribute("password");
        session.setAttribute("password",null);
        if(temp.isPresent())
        {
            return "Already Exist";
        }
        WebUser user = userMapper.toEntity(userDTO);
        user.setJoinDate(LocalDate.now());
        user.setRole(UserRole.USER);
        user.setStatus(true);
        if(password != null) {
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);
        }
        userRepository.save(user);
        return "Successfully added";
    }


    public List<String> saveImagesToDirectory(List<MultipartFile> images,String articleCode,String productName) throws IOException {
        final String IMAGE_DIR = "src/main/resources/static/assets/images/product/";
        List<String> paths = new ArrayList<>();

        // Ensure the directory exists
        File directory = new File(IMAGE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save each uploaded image
        int x=1;
        for (MultipartFile image : images) {
            String imageName = productName.replaceAll("\\s+", "")+"_"+articleCode+"_"+x+".jpeg";
            x++;
            paths.add("/assets/images/product/"+imageName);
            Path imagePath = Paths.get(IMAGE_DIR + imageName);
            Files.write(imagePath, image.getBytes());
        }
        return paths;
    }

    public boolean resetPassword(ResetPasswordDTO user) {
        WebUser userTemp = userRepository.findByEmail(user.getEmail()).orElse(new WebUser());
        if(passwordEncoder.matches(user.getPassword(),userTemp.getPassword()))
            return false;
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        userTemp.setPassword(hashedPassword);
        userRepository.save(userTemp);
        return true;
    }

    public void addProductVariant(String articleCode, String colorCode, String color, Double price, Integer quantity, Integer maxQuantity, String[] size, List<MultipartFile> images, Long productId) throws IOException {
        Product product = productRepository.findById(productId).orElse(new Product());
        ProductVariant productVariant = new ProductVariant();
        productVariant.setProduct(product);
        productVariant.setColorCode(colorCode);
        productVariant.setColor(color);
        productVariant.setQuantity(quantity);
        productVariant.setMaxQuantity(maxQuantity);
        productVariant.setSize(size);
        productVariant.setArticleCode(articleCode);
        productVariant.setPrice(price);
        List<String> paths = saveImagesToDirectory(images,articleCode,product.getName());
        productVariant.setImages(paths);
        productVariant.setStockStatus("AVAILABLE");
        productVariantRepository.save(productVariant);
    }

    public ResponseEntity<?> changePassword(ChangePassword changePassword, WebUser user)
    {
        if(passwordEncoder.matches(changePassword.getCurrentPassword(),user.getPassword()))
        {
            String hashedPassword = passwordEncoder.encode(changePassword.getNewPassword());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return ResponseEntity.ok("success");
        }
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("current password is incorrect");


    }
}
