package com.ecom.cart.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfoVo {
    private String userId;
    private String userKey;
}
