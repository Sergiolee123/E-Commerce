package com.ecom.cart.controller;

import com.ecom.cart.service.CartService;
import com.ecom.cart.vo.Cart;
import com.ecom.common.utils.R;
import org.springframework.web.bind.annotation.*;

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
    public R cartList() {
        return cartService.getCart();
    }

    @PostMapping
    public R addCart(@RequestBody @Valid @NotNull Cart cart) {
        return cartService.createCart(cart);
    }

    @PutMapping
    public R updateCart(@RequestBody @Valid @NotNull Cart cart) {
        return cartService.updateCart(cart);
    }

    @DeleteMapping
    public R deleteCart() {
        return cartService.deleteCart();
    }
}
