package com.e_commerce.SNEAKERHEAD.Security;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import com.e_commerce.SNEAKERHEAD.Service.AdminManagementService;
import com.e_commerce.SNEAKERHEAD.Service.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    AdminManagementService adminManagementService;
    @Autowired
    UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/x-www-form-urlencoded");
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "Login successful!");
        responseData.put("user", authentication.getName());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
        Object principal = authentication.getPrincipal();
        if(principal instanceof UserPrincipal) {
            String role = ((UserPrincipal) principal).getRole();
            String email=((UserPrincipal) principal).getUsername();
            HttpSession session = request.getSession();
            session.setAttribute("role", role);
            session.setAttribute("userEmail",email);
            WebUser user = userRepository.findByEmail(email).orElse(new WebUser());
            String userName = user.getFullName();
            session.setAttribute("userName",userName);
            String redirectUrl = "/";
            new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
        else if(principal instanceof DefaultOidcUser)
        {
            DefaultOidcUser defaultOidcUser = (DefaultOidcUser) principal;
            String email = defaultOidcUser.getEmail();
            String name = defaultOidcUser.getFullName();
            WebUser user=new WebUser();
            user.setFullName(name);
            user.setEmail(email);
            user.setStatus(true);
            if(!userRepository.existsByEmail(email))
            {
                userRepository.save(user);
            }
            HttpSession session = request.getSession();
            session.setAttribute("role","USER");
            session.setAttribute("userEmail",email);
            session.setAttribute("userName",name);
            String redirectUrl = "/";
            new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
