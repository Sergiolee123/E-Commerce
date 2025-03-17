package com.ecom.cart.service.impl;

import com.ecom.cart.constants.CartConstant;
import com.ecom.cart.interceptor.CartInterceptor;
import com.ecom.cart.service.CartService;
import com.ecom.cart.vo.Cart;
import com.ecom.common.exception.BizCodeEnum;
import com.ecom.common.utils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public CartServiceImpl(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    private String getRedisKey(String userKey) {
        return CartConstant.CART_REDIS_KEY_PREFIX + userKey;
    }


    @Override
    public R getCart() {
        String userKey = CartInterceptor.threadLocal.get().getUserKey();
        if (StringUtils.isEmpty(userKey)) {
            return R.error(BizCodeEnum.VALID_EXCEPTION);
        }

        String cartStr = stringRedisTemplate.opsForValue().get(getRedisKey(userKey));
        if (StringUtils.isNotEmpty(cartStr)) {
            try {
                Cart cart = objectMapper.readValue(cartStr, Cart.class);
                return R.ok().put("cart", cart);
            } catch (Exception e) {
                log.info("cannot read redis cart value json", e);
                return R.error();
            }
        }
        return R.error(BizCodeEnum.RESULT_NOT_FOUND_EXCEPTION);
    }

    @Override
    public R updateCart(Cart cart) {
        String userKey = CartInterceptor.threadLocal.get().getUserKey();
        if (StringUtils.isEmpty(userKey) || Boolean.FALSE.equals(stringRedisTemplate.hasKey(userKey))) {
            return R.error(BizCodeEnum.RESULT_NOT_FOUND_EXCEPTION);
        }
        try {
            stringRedisTemplate.opsForValue().set(getRedisKey(userKey), objectMapper.writeValueAsString(cart));
        } catch (Exception e) {
            log.info("cannot write redis cart value json", e);
            return R.error(BizCodeEnum.UNKNOW_EXCEPTION);
        }

        return R.ok();

    }

    @Override
    public R createCart(Cart cart) {
        String userKey = CartInterceptor.threadLocal.get().getUserKey();;
        try {
            stringRedisTemplate.opsForValue().set(getRedisKey(userKey), objectMapper.writeValueAsString(cart));
        } catch (JsonProcessingException e) {
            log.info("cannot write redis cart value json", e);
            return R.error(BizCodeEnum.UNKNOW_EXCEPTION);
        }

        return R.ok();
    }

    @Override
    public R deleteCart() {
        String userKey = CartInterceptor.threadLocal.get().getUserKey();
        stringRedisTemplate.delete(getRedisKey(userKey));

        return R.ok();
    }
}
