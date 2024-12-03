package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
     private BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

     @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
         WebUser user=userRepository.findByEmail(email)
                 .orElseThrow(()-> new UsernameNotFoundException("User not Found"));
         return new UserPrincipal(user);
     }

}
