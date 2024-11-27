package com.ecom.auth.feign;

import com.ecom.auth.vo.MemberLoginVo;
import com.ecom.auth.vo.MemberRegistryVo;
import com.ecom.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ecom-member")
public interface MemberFeignService {
    @PostMapping("/api/v1/member/registry")
    R registry(@RequestBody MemberRegistryVo memberRegistryVo);

    @PostMapping("/api/v1/member/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo);
}
