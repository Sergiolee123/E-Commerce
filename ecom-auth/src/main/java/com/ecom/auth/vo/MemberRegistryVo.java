package com.ecom.auth.vo;

import lombok.Data;

@Data
public class MemberRegistryVo {
    private String username;
    private String password;
    private String phone;
    private String email;

}
