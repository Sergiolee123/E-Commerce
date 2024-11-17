package com.ecom.auth.service.impl;

import com.ecom.auth.feign.ThirdPartFeignService;
import com.ecom.auth.service.AuthService;
import com.ecom.auth.util.AuthUtil;
import com.ecom.common.constant.AuthConstant;
import com.ecom.common.exception.BizCodeEnum;
import com.ecom.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final ThirdPartFeignService thirdPartFeignService;
    private final StringRedisTemplate redisTemplate;
    private final ThreadPoolExecutor executor;

    public AuthServiceImpl(ThirdPartFeignService thirdPartFeignService, StringRedisTemplate redisTemplate, ThreadPoolExecutor executor) {
        this.thirdPartFeignService = thirdPartFeignService;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    @Override
    public R sendVerifyCodeToEmail(String email) {
        String key = AuthConstant.EMAIL_CODE_CACHE_PREFIX + email;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return R.error(BizCodeEnum.VALID_EXCEPTION);
        }
        CompletableFuture.runAsync(() -> {
            String verificationCode = AuthUtil.getVerificationCode();
            redisTemplate.opsForValue().set(key, verificationCode, 5, TimeUnit.MINUTES);
            thirdPartFeignService.sendCode(email, verificationCode);
            log.info("send verification code success to email: {}", email);
        }, executor);
        return R.ok();
    }
}
