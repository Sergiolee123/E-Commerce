package com.ecom.auth.util;

import java.util.UUID;

public class AuthUtil {
    public static String getVerificationCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
    }
}
