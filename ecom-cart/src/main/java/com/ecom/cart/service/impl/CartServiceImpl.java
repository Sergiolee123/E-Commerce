package com.ecom.cart.service.impl;

import com.ecom.cart.constants.CartConstant;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public CartServiceImpl(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    private String getUserKey(HttpServletRequest request) {
        String userKey = null;
        for (Cookie cookie : request.getCookies()) {
            if (Objects.equals(cookie.getName(), CartConstant.USER_KEY_COOKIE_NAME)) {
                userKey = cookie.getValue();
            }
        }
        return userKey;
    }

    private String getRedisKey(String userKey) {
        return CartConstant.CART_REDIS_KEY_PREFIX + userKey;
    }


    @Override
    public R getCart(HttpServletRequest request) {
        String userKey = getUserKey(request);
        if (StringUtils.isEmpty(getUserKey(request))) {
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
    public R updateCart(Cart cart, HttpServletRequest request, HttpServletResponse response) {
        String userKey = getUserKey(request);
        if (StringUtils.isEmpty(userKey) || Boolean.FALSE.equals(stringRedisTemplate.hasKey(userKey))) {
            return R.error(BizCodeEnum.RESULT_NOT_FOUND_EXCEPTION);
        }
        try {
            stringRedisTemplate.opsForValue().set(getRedisKey(userKey), objectMapper.writeValueAsString(cart));
        } catch (Exception e) {
            log.info("cannot write redis cart value json", e);
            return R.error(BizCodeEnum.UNKNOW_EXCEPTION);
        }

        Cookie userKeyCookie = new Cookie(CartConstant.USER_KEY_COOKIE_NAME, userKey);
        userKeyCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(userKeyCookie);

        return R.ok();

    }

    @Override
    public R createCart(Cart cart, HttpServletRequest request, HttpServletResponse response) {
        String userKey = getUserKey(request);
        try {
            stringRedisTemplate.opsForValue().set(getRedisKey(userKey), objectMapper.writeValueAsString(userKey));
        } catch (JsonProcessingException e) {
            log.info("cannot write redis cart value json", e);
            return R.error(BizCodeEnum.UNKNOW_EXCEPTION);
        }

        Cookie userKeyCookie = new Cookie(CartConstant.USER_KEY_COOKIE_NAME, userKey);
        userKeyCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(userKeyCookie);

        return R.ok();
    }

    @Override
    public R deleteCart(HttpServletRequest request, HttpServletResponse response) {
        String userKey = getUserKey(request);
        stringRedisTemplate.delete(getRedisKey(userKey));

        Cookie userKeyCookie = new Cookie(CartConstant.USER_KEY_COOKIE_NAME, null);
        userKeyCookie.setMaxAge(0);
        response.addCookie(userKeyCookie);

        return R.ok();
    }
}
