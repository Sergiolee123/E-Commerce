package com.ecom.cart.interceptor;

import com.ecom.cart.constants.CartConstant;
import com.ecom.cart.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * if @ResponseBody or ResponseEntity is being used, nor postHandle and afterCompletion method can modify the response.
 */
@Slf4j
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userKey = getUserKey(request);
        threadLocal.set(UserInfoVo.builder().userId("1").userKey(userKey).build());
        return true;
    }

    /**
     * <herf a="https://docs.spring.io/spring-framework/docs/5.2.23.RELEASE/spring-framework-reference/web.html#mvc-handlermapping-interceptor">Why cookie cannot be set while using HandlerInterceptor with @ResponseBody and ResponseEntity</herf>
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // below code will not work if @ResponseBody and ResponseEntity is being used in the controller layer
 /*       Cookie userKeyCookie = new Cookie(CartConstant.USER_KEY_COOKIE_NAME, threadLocal.get().getUserKey());
        userKeyCookie.setMaxAge(24 * 60 * 60 * 30);
        response.addCookie(userKeyCookie);*/
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }

    private String getUserKey(HttpServletRequest request) {
        String userKey = null;
        for (Cookie cookie : Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{})) {
            if (Objects.equals(cookie.getName(), CartConstant.USER_KEY_COOKIE_NAME)) {
                userKey = cookie.getValue();
            }
        }
        return userKey==null? UUID.randomUUID().toString():userKey;
    }
}
