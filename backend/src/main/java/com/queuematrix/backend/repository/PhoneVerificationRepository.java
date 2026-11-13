package com.queuematrix.backend.repository;

import com.queuematrix.backend.entity.PhoneVerification;
import com.queuematrix.backend.entity.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification> findTopByPhoneAndPurposeOrderByIdDesc(String phone, VerificationPurpose purpose);

    Optional<PhoneVerification> findByVerificationTokenAndPurpose(String verificationToken, VerificationPurpose purpose);

    @Query("SELECT COUNT(pv) FROM PhoneVerification pv WHERE pv.phone = :phone AND pv.createdAt >= :since")
    long countRequestsSince(@Param("phone") String phone, @Param("since") LocalDateTime since);
}
