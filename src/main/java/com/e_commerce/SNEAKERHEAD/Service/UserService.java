package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.AddressDTO;
import com.e_commerce.SNEAKERHEAD.DTO.UsersList;
import com.e_commerce.SNEAKERHEAD.Entity.Cart;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Mappers.AddressMapper;
import com.e_commerce.SNEAKERHEAD.Repository.AddressRepository;
import com.e_commerce.SNEAKERHEAD.Repository.CartRepository;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    private JavaMailSender mailSender;

    public List<UsersList> ListUser()
    {
        List<WebUser> list = userRepository.findAll();
        List<UsersList> users = new ArrayList<>();
        for(WebUser x : list)
        {
            UsersList usersList = new UsersList();
            usersList.setId(x.getId());
            usersList.setName(x.getFullName());
            usersList.setDate(x.getJoinDate().toString());
            usersList.setPhone(x.getPhone());
            usersList.setEmail(x.getEmail());
            usersList.setStatus(x.getStatus());
            users.add(usersList);
        }
        return users;
    }
    public boolean addAddress(AddressDTO addressDto, WebUser user)
    {
        UserAddress address;
        List<UserAddress> tempList = addressRepository.findAllByUser_idAndStatus(user.getId(), "AVAILABLE");
        if(!tempList.isEmpty())
        {
            for(UserAddress x : tempList)
            {
                if(x.getName().contains(addressDto.getName()) && x.getCity().contains(addressDto.getCity()) && x.getCountry().contains(addressDto.getCountry()) && x.getBuilding().contains(addressDto.getBuilding()) && x.getState().contains(addressDto.getState()) && x.getStreet().contains(addressDto.getStreet()))
                {
                    return false;
                }
            }
        }
        if(tempList.isEmpty())
        {
            addressDto.setDefaultAddressStatus(true);
        }
        else if(addressDto.isDefaultAddressStatus())
        {
            for(UserAddress x : tempList)
            {
                if(x.getDefaultAddressStatus())
                {
                    x.setDefaultAddressStatus(false);
                    addressRepository.save(x);

                }
            }
        }
        else
        {
            addressDto.setDefaultAddressStatus(false);
        }
        address = addressMapper.toEntity(addressDto);
        address.setStatus("AVAILABLE");
        address.setUser(user);
        addressRepository.save(address);
        return true;
    }

    public String addCart(WebUser user, ProductVariant variant,Integer quantity)
    {
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId());
        for(Cart cart : carts)
        {
            if(cart.getProductVariant().getId() == variant.getId())
            {
                cart.setQuantity(cart.getQuantity()+quantity );
                return "Already exist, Quantity added";
            }
        }
        Cart temp = new Cart();
        temp.setUser(user);
        temp.setProductVariant(variant);
        temp.setQuantity(quantity);
        cartRepository.save(temp);
        return "Cart added successfully";
    }

    public String generateReferralCode(Long userId) {
        // You can customize this logic to make it more user-specific
        String randomPart = UUID.randomUUID().toString().substring(0, 3); // 8 chars of random UUID
        return "REF-" + userId+ randomPart;  // Custom format: REF-USERID-RANDOM
    }
    public void sendReferralCodeEmail(String Email,String referralCode)
    {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(Email);
        message.setSubject("You will get ₹1000 off");
        message.setText("Referral code : "+referralCode);
        mailSender.send(message);
    }
}
