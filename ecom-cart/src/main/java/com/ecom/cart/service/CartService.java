package com.ecom.cart.service;

import com.ecom.cart.vo.Cart;
import com.ecom.common.utils.R;

public interface CartService {
    R getCart();

    R updateCart(Cart cart);

    R createCart(Cart cart);

    R deleteCart();
}
