package com.ecom.member.controller;

import com.ecom.common.utils.R;
import com.ecom.member.service.MemberService;
import com.ecom.member.vo.MemberLoginVo;
import com.ecom.member.vo.MemberRegistryVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
public class ServiceController {

    private final MemberService memberService;

    public ServiceController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/registry")
    public R registry(@RequestBody MemberRegistryVo memberRegistryVo){
        return memberService.registry(memberRegistryVo);
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo) {

        return memberService.login(memberLoginVo);
    }

}
