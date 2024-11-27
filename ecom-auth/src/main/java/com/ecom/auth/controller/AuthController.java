package com.ecom.auth.controller;

import com.ecom.auth.service.AuthService;
import com.ecom.auth.vo.UserLoginVo;
import com.ecom.auth.vo.UserRegistryVo;
import com.ecom.common.utils.R;
import lombok.SneakyThrows;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/email/sendcode")
    public R sendCode(@RequestParam("email") String email) {
        return authService.sendVerifyCodeToEmail(email);
    }

    @SneakyThrows
    @PostMapping("/registry")
    public R registry(@RequestBody @Validated UserRegistryVo userRegistryVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errMap = bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors
                            .toMap(FieldError::getField,
                                    (error) -> error.getDefaultMessage() == null ? "error" : error.getDefaultMessage(),
                                    (v1, v2) -> v1 + " : " + v2,
                                    TreeMap::new
                            )
                    );
            return R.error().put("data", errMap);
        }

        return authService.registry(userRegistryVo);
    }

    @PostMapping("/login")
    public R login(@RequestBody @Validated UserLoginVo userLoginVo) {

        return authService.login(userLoginVo);
    }
}
