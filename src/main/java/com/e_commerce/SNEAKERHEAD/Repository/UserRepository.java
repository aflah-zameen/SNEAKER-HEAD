package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<WebUser,Long> {
    Optional<WebUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
