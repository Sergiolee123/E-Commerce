package com.ecom.auth.service.impl;

import com.ecom.auth.feign.ThirdPartFeignService;
import com.ecom.auth.service.AuthService;
import com.ecom.auth.util.AuthUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final ThirdPartFeignService thirdPartFeignService;

    public AuthServiceImpl(ThirdPartFeignService thirdPartFeignService) {
        this.thirdPartFeignService = thirdPartFeignService;
    }

    @Async
    @Override
    public void sendVerifyCodeToEmail(String email) {
        String verificationCode = AuthUtil.getVerificationCode();
        thirdPartFeignService.sendCode(email, verificationCode);
    }
}
