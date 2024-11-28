package com.ecom.cart.controller;

import com.ecom.cart.service.CartService;
import com.ecom.cart.vo.Cart;
import com.ecom.common.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public R cartList(HttpServletRequest request) {
        return cartService.getCart(request);
    }

    @PostMapping
    public R addCart(@RequestBody @Valid @NotNull Cart cart, HttpServletRequest request, HttpServletResponse response) {
        return cartService.createCart(cart, request, response);
    }

    @PutMapping
    public R updateCart(@RequestBody @Valid @NotNull Cart cart, HttpServletRequest request, HttpServletResponse response) {
        return cartService.updateCart(cart, request, response);
    }

    @DeleteMapping
    public R deleteCart(HttpServletRequest request, HttpServletResponse response) {
        return cartService.deleteCart(request, response);
    }
}
