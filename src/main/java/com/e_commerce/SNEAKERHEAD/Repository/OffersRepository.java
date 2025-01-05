package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface OffersRepository extends JpaRepository<Offer,Long> {
    List<Offer> findAllByIsActive(Boolean status);

    List<Offer> findByTypeAndTypeIdAndEndDateGreaterThanEqual(String product, Long id, LocalDate now);
}
