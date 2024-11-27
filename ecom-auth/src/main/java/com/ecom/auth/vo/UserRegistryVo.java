package com.ecom.auth.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRegistryVo {

    @NotBlank
    @Size(min = 2, max = 20)
    private String username;
    @NotBlank
    @Size(min = 2, max = 20)
    private String password;
    @NotBlank
    private String phone;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 5, max = 5)
    private String code;

}
