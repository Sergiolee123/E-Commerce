package com.ecom.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("ecom-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    com.ecom.common.utils.R memberCoupons();
}
