package com.example.demosendmail.service;

import com.example.demosendmail.model.User;
import com.example.demosendmail.model.VerificationToken;
import com.example.demosendmail.reposotory.UserRepository;
import com.example.demosendmail.reposotory.VerificationTokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationTokenService {
    private UserRepository userRepository;
    private VerificationTokeRepository verificationTokeRepository;
    private SendingMailService sendingMailService;

    @Autowired
    public VerificationTokenService(UserRepository userRepository,
                                    VerificationTokeRepository verificationTokeRepository,
                                    SendingMailService sendingMailService){
        this.userRepository = userRepository;
        this.verificationTokeRepository = verificationTokeRepository;
        this.sendingMailService = sendingMailService;
    }
    public void createVerification(String email){
        List<User> users = userRepository.findByEmail(email);
        User user;
        if (users.isEmpty()){
            user = new User();
            user.setEmail(email);
            userRepository.save(user);
        }else {
            user = users.get(0);
        }
        List<VerificationToken> verificationTokens = verificationTokeRepository.findByUserEmail(email);
        VerificationToken verificationToken;
        if (verificationTokens.isEmpty()){
            verificationToken = new VerificationToken();
            verificationToken.setUser(user);
            verificationTokeRepository.save(verificationToken);
        }else {
            verificationToken = verificationTokens.get(0);
        }
        sendingMailService.sendVerificationMail(email,verificationToken.getToke());
    }
    public ResponseEntity<String> verifyEmail(String token){
        List<VerificationToken> verificationTokens = verificationTokeRepository.findByToke(token);
        if (verificationTokens.isEmpty()){
            return ResponseEntity.badRequest().body("Invalid token.");
        }
        VerificationToken verificationToken = verificationTokens.get(0);
        if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())){
            return ResponseEntity.unprocessableEntity().body("Expired token.");
        }
        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.setStatus(VerificationToken.STATUS_VERIFIED);
        verificationTokeRepository.save(verificationToken);
        return ResponseEntity.ok("You have successfully verified tour email address.");
    }

}