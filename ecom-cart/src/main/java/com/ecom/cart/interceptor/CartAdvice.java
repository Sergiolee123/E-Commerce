package com.ecom.cart.interceptor;

import com.ecom.cart.constants.CartConstant;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.Cookie;

@RestControllerAdvice
public class CartAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Cookie userKeyCookie = new Cookie(CartConstant.USER_KEY_COOKIE_NAME, CartInterceptor.threadLocal.get().getUserKey());
        userKeyCookie.setMaxAge(24 * 60 * 60 * 30);
        ((ServletServerHttpResponse)response).getServletResponse().addCookie(userKeyCookie);
        return body;
    }
}
