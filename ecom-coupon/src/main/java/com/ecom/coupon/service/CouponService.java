package com.ecom.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecom.common.utils.PageUtils;
import com.ecom.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 22:47:41
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

