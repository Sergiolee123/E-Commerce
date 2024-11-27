package com.ecom.auth.service;

import com.ecom.auth.vo.UserLoginVo;
import com.ecom.auth.vo.UserRegistryVo;
import com.ecom.common.utils.R;

public interface AuthService {
    R sendVerifyCodeToEmail(String email);

    R registry(UserRegistryVo userRegistryVo);

    R login(UserLoginVo userLoginVo);
}
