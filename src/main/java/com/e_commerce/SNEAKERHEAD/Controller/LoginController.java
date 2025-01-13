package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.ResetPasswordDTO;
import com.e_commerce.SNEAKERHEAD.DTO.UserDTO;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Mappers.UserMapper;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import com.e_commerce.SNEAKERHEAD.Service.AdminManagementService;
import com.e_commerce.SNEAKERHEAD.Service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    OtpService otpService;
    @Autowired
    AdminManagementService adminManagementService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/")
    public String HomePage(HttpSession session)
    {
        if(session != null && session.getAttribute("role")!=null)
        {
            String role = (String) session.getAttribute("role");
            if("ADMIN".equals(role))
            {
                return "redirect:/admin/product";
            }
            else if("USER".equals(role))
            {
                return "redirect:/user/shop";
            }
        }
        return "redirect:/user/shop";
    }
    @GetMapping("/userlogin")
    public String LoginPage(HttpSession session)
    {
        if(session != null && session.getAttribute("role") != null)
        {
          return "redirect:/";
        }
        return "/userlogin";
    }
    @GetMapping("/signup")
    public String Signup(Model model){
        model.addAttribute("user",new WebUser());
        return "signup";
    }

    @ResponseBody
    @PostMapping(value = "/signup/userdata", consumes = "application/json")
    public ResponseEntity<?> registerUser(@Valid  @RequestBody WebUser user,HttpServletRequest request) {
        System.out.println(user.getFullName());
        Map<String, String> response = new HashMap<>();
        UserDTO userDTO = userMapper.toDTO(user);
        try {
            // Check if email is already registered (assuming userService handles this check)
            if (userRepository.existsByEmail(user.getEmail())) {
                response.put("message", "Email is already in use.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Generate OTP and send email
            String otp = otpService.generateOtp();
            otpService.sendOtpEmail(user.getEmail(), otp);

            // Save user details and OTP in session
            HttpSession session = request.getSession();
            session.setAttribute("userdata", userDTO);
            session.setAttribute("password",user.getPassword());
            session.setAttribute("otp", otp);
            session.setAttribute("forgetPassword",false);
            response.put("message", "User registered successfully.");
            response.put("redirectUrl", "/otpverification");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/otpverification")
    public String Verification(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        session.setAttribute("expirationTime",expirationTime);
        UserDTO user = (UserDTO) session.getAttribute("userdata");
        String email = user.getEmail();
        model.addAttribute("email",email);
        return "otpverification";
    }

    @GetMapping("/otpverification/{email}")
    public String Verification(@PathVariable String email,HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        session.setAttribute("forgetPassword",true);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        session.setAttribute("expirationTime",expirationTime);
        model.addAttribute("email",email);
        return "otpverification";
    }

    @PostMapping("/otpverification/data")
    public ResponseEntity<Map<String, Object>> checkOtp(HttpServletRequest request, @RequestParam("userOtp") String userOtp)
    {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession();
        String Otp = (String)session.getAttribute("otp");
        LocalDateTime expirationTime =(LocalDateTime) session.getAttribute("expirationTime");
        if((boolean)session.getAttribute("forgetPassword") )
        {
            response.put("forgetPassword",true);
        }
        else{
            response.put("forgetPassword",false);
        }
        System.out.println(LocalDateTime.now()+">>"+expirationTime);
        if(Otp.equals(userOtp) && LocalDateTime.now().isBefore(expirationTime) )
        {
            UserDTO userDTO =(UserDTO) session.getAttribute("userdata");
            adminManagementService.addUser(userDTO,session);
            response.put("verified", true);
        }
        else
        {
            response.put("verified", false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, Object>> resendOtp(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        String otp = otpService.generateOtp();
        HttpSession session =request.getSession();
        UserDTO user =(UserDTO) session.getAttribute("userdata");
        otpService.sendOtpEmail(user.getEmail(), otp);
        session.setAttribute("otp", otp);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        System.out.println(expirationTime);
        session.setAttribute("expirationTime",expirationTime);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forgetpassword")
    public String showForgetPassword()
    {
        return "forget_password";
    }
    @ResponseBody
    @PostMapping("/forgetpassword/{email}")
    public ResponseEntity<?> forgetPassword(@PathVariable String email,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail(email).orElse(new WebUser());
        UserDTO userDTO = userMapper.toDTO(user);
        session.setAttribute("userdata",userDTO);
        if(user.getId() == null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("user not found");
        // Generate OTP and send email
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(user.getEmail(), otp);
        session.setAttribute("otp", otp);
        return ResponseEntity.status(HttpStatus.OK).body("user found");
    }

    @GetMapping("/resetpassword")
    public String showResetPassword(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        UserDTO user =(UserDTO) session.getAttribute("userdata");
        String email = user.getEmail();
        model.addAttribute("email",email);
        return "reset_password";
    }

    @ResponseBody
    @PostMapping("/resetpassword/data")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO, BindingResult result)
    {
        if(result.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        if(adminManagementService.resetPassword(resetPasswordDTO))
        {
            return ResponseEntity.status(HttpStatus.OK).body("password changed successfully");
        }
        else
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The password same as your previous!");
        }

    }
}
