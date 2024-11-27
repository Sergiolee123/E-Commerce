package com.ecom.auth.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserLoginVo {

    @NotBlank
    @Size(min = 2, max = 20)
    private String username;
    @NotBlank
    @Size(min = 2, max = 20)
    private String password;
}
