package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.AddressDto;
import com.e_commerce.SNEAKERHEAD.DTO.UsersList;
import com.e_commerce.SNEAKERHEAD.Entity.Cart;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.AddressRepository;
import com.e_commerce.SNEAKERHEAD.Repository.CartRepository;
import com.e_commerce.SNEAKERHEAD.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CartRepository cartRepository;

    public List<UsersList> ListUser()
    {
        List<WebUser> list = userRepository.findAll();
        List<UsersList> users = new ArrayList<>();
        for(WebUser x : list)
        {
            UsersList usersList = new UsersList();
            usersList.setId(x.getId());
            usersList.setName(x.getFull_name());
            usersList.setDate(x.getJoin_date().toString());
            usersList.setPhone(x.getPhone());
            usersList.setEmail(x.getEmail());
            users.add(usersList);
        }
        return users;
    }
    public boolean addAddress(AddressDto addressDto,WebUser user)
    {
        UserAddress address = new UserAddress();
        List<UserAddress> tempList = addressRepository.findAllByUser_idAndStatus(user.getId(), "AVAILABLE");
        if(tempList.isEmpty())
        {
            address.setDefaultAddressStatus(true);
            address.setUser(user);
            address.setName(addressDto.getName());
            address.setPhone(addressDto.getPhone());
            address.setBuilding(addressDto.getBuilding());
            address.setCity(addressDto.getCity());
            address.setCountry(addressDto.getCountry());
            address.setState(addressDto.getState());
            address.setLandmark(addressDto.getLandmark());
            address.setInstruction(addressDto.getInstructions());
            address.setZipCode(addressDto.getZipCode());
            address.setInstruction(addressDto.getInstructions());
            address.setType(addressDto.getType());
            address.setStreet(addressDto.getStreet());
            address.setStatus("AVAILABLE");
            addressRepository.save(address);
            return true;
        }
        for(UserAddress x : tempList)
        {
            if(x.getName().contains(addressDto.getName()) && x.getCity().contains(addressDto.getCity()) && x.getCountry().contains(addressDto.getCountry()) && x.getBuilding().contains(addressDto.getBuilding()) && x.getState().contains(addressDto.getState()) && x.getStreet().contains(addressDto.getStreet()))
            {
                return false;
            }
        }
        if(addressDto.isDefaultAddress())
        {
            for(UserAddress x : tempList)
            {
                if(x.getDefaultAddressStatus()==true)
                {
                    x.setDefaultAddressStatus(false);
                    addressRepository.save(x);
                    address.setDefaultAddressStatus(true);
                    address.setUser(user);
                    address.setName(addressDto.getName());
                    address.setPhone(addressDto.getPhone());
                    address.setBuilding(addressDto.getBuilding());
                    address.setCity(addressDto.getCity());
                    address.setCountry(addressDto.getCountry());
                    address.setState(addressDto.getState());
                    address.setLandmark(addressDto.getLandmark());
                    address.setInstruction(addressDto.getInstructions());
                    address.setZipCode(addressDto.getZipCode());
                    address.setInstruction(addressDto.getInstructions());
                    address.setType(addressDto.getType());
                    address.setStreet(addressDto.getStreet());
                    address.setStatus("AVAILABLE");
                    addressRepository.save(address);
                    return true;

                }
            }
        }
        address.setDefaultAddressStatus(false);
        address.setUser(user);
        address.setName(addressDto.getName());
        address.setPhone(addressDto.getPhone());
        address.setBuilding(addressDto.getBuilding());
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setState(addressDto.getState());
        address.setLandmark(addressDto.getLandmark());
        address.setInstruction(addressDto.getInstructions());
        address.setZipCode(addressDto.getZipCode());
        address.setInstruction(addressDto.getInstructions());
        address.setType(addressDto.getType());
        address.setStreet(addressDto.getStreet());
        address.setStatus("AVAILABLE");
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
}
