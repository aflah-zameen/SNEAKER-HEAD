package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute("userName")
    public String addUserToModel(HttpSession session)
    {
        return (String) session.getAttribute("userName");
    }
    @ModelAttribute("wishlistCount")
    public Integer wishlistCount(HttpSession session)
    {
        return (Integer) session.getAttribute("wishlistCount");
    }
}
