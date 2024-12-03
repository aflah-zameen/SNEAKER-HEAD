package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.ProductRequest;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class UserManagementService extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    UserRepository userRepository;


    BCryptPasswordEncoder passwordEncoder;
    public UserManagementService()
    {
        this.passwordEncoder =new BCryptPasswordEncoder();
    }
//    public WebUser findUserById(Long id) {
//        return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not Found"));
//    }

}
