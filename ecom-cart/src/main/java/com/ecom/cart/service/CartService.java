package com.ecom.cart.service;

import com.ecom.cart.vo.Cart;
import com.ecom.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CartService {
    R getCart(HttpServletRequest request);

    R updateCart(Cart cart, HttpServletRequest request, HttpServletResponse response);

    R createCart(Cart cart, HttpServletRequest request, HttpServletResponse response);

    R deleteCart(HttpServletRequest request, HttpServletResponse response);
}
