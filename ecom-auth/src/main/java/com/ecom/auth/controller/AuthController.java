package com.ecom.auth.controller;

import com.ecom.auth.service.AuthService;
import com.ecom.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/email/sendcode")
    public R sendCode(@RequestParam("email") String email) {
        return authService.sendVerifyCodeToEmail(email);
    }
}
