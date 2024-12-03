package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<UserAddress,Long> {
    List<UserAddress> findAllByUser_idAndStatus(Long  userId,String status);
    Optional<UserAddress> findByUser_idAndDefaultAddressStatusAndStatus(Long id, boolean defaultValue,String status);
}
