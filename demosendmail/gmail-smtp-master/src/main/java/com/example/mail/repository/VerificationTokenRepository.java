package com.example.mail.repository;

import com.example.mail.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByUserEmail(String email);
    VerificationToken findByToken(String token);
}
