package com.e_commerce.SNEAKERHEAD.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;

public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dkbj8um2a",
                "api_key", "975589754275326",
                "api_secret", "iMNaqTmBiYX8LOkO6j9N9AcBuxI"));
    }
}
