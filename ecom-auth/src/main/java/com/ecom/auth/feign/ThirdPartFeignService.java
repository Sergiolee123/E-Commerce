package com.ecom.auth.feign;

import com.ecom.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ecom-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/email/sendcode")
    R sendCode(@RequestParam("toEmail") String toEmail, @RequestParam("code") String code);

}
