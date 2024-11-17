package com.ecom.thirdParty.controller;

import com.ecom.common.constant.AuthConstant;
import com.ecom.common.utils.R;
import com.ecom.thirdParty.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final NotificationService notificationService;

    public EmailController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("toEmail") String toEmail,@RequestParam("code") String code) {
        notificationService.sendEmail(toEmail, AuthConstant.VERIFICATION_CODE, code);
        return R.ok();
    }
}
