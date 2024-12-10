package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    @Autowired
    UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("given_name");
        String email = oAuth2User.getAttribute("email");

        // Check if user already exists in the database
        Optional<WebUser> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            // New user - create and save
            WebUser newUser = new WebUser();
            newUser.setFullName(name);
            newUser.setEmail(email);

            userRepository.save(newUser);
        }

        return oAuth2User;
    }
}
