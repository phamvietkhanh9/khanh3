package com.example.demosendmail.web;


import com.example.demosendmail.model.VerificationFrom;
import com.example.demosendmail.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
public class AccountController {
    @Autowired
    VerificationTokenService verificationTokenService;

    @GetMapping("/")
    public String index(){
        return "redirect:/email-verification";}
    @GetMapping("/email-verification")
    public String fromGet(Model model){
        model.addAttribute("verificationForm", new VerificationFrom());
        return "verification-form";
    }
    @PostMapping("/email-verification")
    public String formPost(@Valid VerificationFrom verificationFrom, BindingResult bindingResult, Model model){
        if (!bindingResult.hasErrors()){
            model.addAttribute("noErrors",true);}
        model.addAttribute("verificationForm",verificationFrom);
        verificationTokenService.createVerification(verificationFrom.getEmail());
        return "verification-from";
    }
    @GetMapping("/verify-email")
    @ResponseBody
    public String verifyEmail(String code){return verificationTokenService.verifyEmail(code).getBody();}
}

