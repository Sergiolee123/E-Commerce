package com.ecom.auth.service.impl;

import com.ecom.auth.feign.MemberFeignService;
import com.ecom.auth.feign.ThirdPartFeignService;
import com.ecom.auth.service.AuthService;
import com.ecom.auth.util.AuthUtil;
import com.ecom.auth.vo.MemberLoginVo;
import com.ecom.auth.vo.MemberRegistryVo;
import com.ecom.auth.vo.UserLoginVo;
import com.ecom.auth.vo.UserRegistryVo;
import com.ecom.common.constant.AuthConstant;
import com.ecom.common.exception.BizCodeEnum;
import com.ecom.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final ThirdPartFeignService thirdPartFeignService;
    private final MemberFeignService memberFeignService;
    private final StringRedisTemplate redisTemplate;
    private final ThreadPoolExecutor executor;
    private final int expire = 5;

    public AuthServiceImpl(ThirdPartFeignService thirdPartFeignService, MemberFeignService memberFeignService, StringRedisTemplate redisTemplate, ThreadPoolExecutor executor) {
        this.thirdPartFeignService = thirdPartFeignService;
        this.memberFeignService = memberFeignService;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    @Override
    public R sendVerifyCodeToEmail(String email) {
        String key = AuthConstant.EMAIL_CODE_CACHE_PREFIX + email;
        Long expireRemindSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if(expireRemindSeconds != null && expireRemindSeconds > 0 && expireRemindSeconds > (expire - 1) * 60) {
            return R.error(BizCodeEnum.VALID_EXCEPTION);
        }
        CompletableFuture.runAsync(() -> {
            String verificationCode = AuthUtil.getVerificationCode();
            redisTemplate.opsForValue().set(key, verificationCode, expire, TimeUnit.MINUTES);
            thirdPartFeignService.sendCode(email, verificationCode);
            log.info("send verification code success to email: {}", email);
        }, executor);
        return R.ok();
    }

    @Override
    public R registry(UserRegistryVo userRegistryVo) {
        String key = AuthConstant.EMAIL_CODE_CACHE_PREFIX + userRegistryVo.getEmail();
        if(!userRegistryVo.getCode().equals(redisTemplate.opsForValue().get(key))){
            return R.error(BizCodeEnum.VALID_EXCEPTION);
        }

        redisTemplate.delete(key);

        MemberRegistryVo vo = new MemberRegistryVo();
        BeanUtils.copyProperties(userRegistryVo, vo);
        return memberFeignService.registry(vo);
    }

    @Override
    public R login(UserLoginVo userLoginVo) {
        MemberLoginVo vo = new MemberLoginVo();
        BeanUtils.copyProperties(userLoginVo, vo);
        return memberFeignService.login(vo);
    }
}
