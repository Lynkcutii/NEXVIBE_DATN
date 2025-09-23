package com.example.datnspct.Controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/util")
public class UtilController {
    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/encode")
    public String encode(@RequestParam String pass) {
        return encoder.encode(pass);
    }
}
