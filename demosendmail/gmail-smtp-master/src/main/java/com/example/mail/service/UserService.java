package com.example.mail.service;

import com.example.mail.entity.User;
import com.example.mail.entity.VerificationToken;
import com.example.mail.repository.UserRepository;
import com.example.mail.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;


    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
    }

    public User createUser(User user) {
        User existedUser = userRepository.findByEmail(user.getEmail());

        if (existedUser != null) {
            return null;
        }
        user.setStatus(false);

        return userRepository.save(user);
    }

    public void sendVerification(User user) {
        VerificationToken token = verificationTokenRepository.findByUserEmail(user.getEmail());

        if (token == null) {
            token = new VerificationToken();
            token.setUser(user);
            verificationTokenRepository.save(token);
        }

        if (token.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            token = new VerificationToken();
            token.setUser(user);
            verificationTokenRepository.save(token);
        }

        mailService.sendVerificationMail(user.getEmail(), token.getToken());

    }

    public boolean verify(String token) {
        VerificationToken existedToken = verificationTokenRepository.findByToken(token);
        if (existedToken == null) {
            return false;
        }

        if (existedToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        existedToken.setConfirmedDateTime(LocalDateTime.now());
        existedToken.setStatus(VerificationToken.VERIFIED);
        existedToken.getUser().setStatus(true);
        verificationTokenRepository.save(existedToken);

        return true;
    }
}
