package com.ecom.auth.service;

import com.ecom.common.utils.R;

public interface AuthService {
    R sendVerifyCodeToEmail(String email);
}
