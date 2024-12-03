package com.e_commerce.SNEAKERHEAD.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private JavaMailSender mailSender;
    private final Random random = new Random();

    public String generateOtp(){
        return String.format("%06d",random.nextInt(1000000));
    }

    public void sendOtpEmail(String Email,String Otp)
    {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(Email);
        message.setSubject("Your OTP code for Verification");
        message.setText("Your OTP code is : "+Otp);
        mailSender.send(message);
    }
}
