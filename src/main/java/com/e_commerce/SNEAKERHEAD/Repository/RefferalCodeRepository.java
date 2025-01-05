package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.ReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefferalCodeRepository extends JpaRepository<ReferralEntity,Long> {
    Boolean existsByUser_id(Long id);
    Optional<ReferralEntity> findByUser_id(Long id);
    Optional<ReferralEntity> findByReferralCode(String code);
}
