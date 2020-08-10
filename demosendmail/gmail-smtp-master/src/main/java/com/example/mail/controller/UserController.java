package com.example.mail.controller;

import com.example.mail.entity.User;
import com.example.mail.service.MailService;
import com.example.mail.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/create")
    public String create(Model model, User user) {
        User newUser = userService.createUser(user);

        if (newUser == null) {
            return "redirect:/";
        }

        userService.sendVerification(newUser);

        return "verify";
    }

    @GetMapping("/verified")
    public String verified(@RequestParam String code) {
        if (code.isEmpty() || code == null) {
            return "redirect:/";
        }
        boolean status = userService.verify(code);

        if (status) {
            return "verified";
        }
        return "fail";
    }
}
