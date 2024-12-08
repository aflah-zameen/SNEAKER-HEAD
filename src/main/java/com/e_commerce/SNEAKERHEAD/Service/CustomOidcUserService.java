package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class CustomOidcUserService extends OidcUserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)throws OAuth2AuthenticationException
    {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        WebUser user = userRepository.findByEmail(email).orElse(new WebUser());
        if(user.getStatus() == null)
        {
            user.setStatus(true);
        }
        if(!user.getStatus() )
        {
            throw new OAuth2AuthenticationException("Access denied for the user");
        }
        return oidcUser;
    }

}
