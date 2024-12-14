package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffersRepository extends JpaRepository<Offer,Long> {
    List<Offer> findAllByIsActive(Boolean status);
}
