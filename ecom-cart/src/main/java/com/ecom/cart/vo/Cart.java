package com.ecom.cart.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public class Cart {
    @Setter
    @Getter
    @NotEmpty
    private List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    @Setter
    @Getter
    private BigDecimal reduce;

    public Integer getCountNum() {
        return items == null ? 0 : items.stream().mapToInt(CartItem::getCount).reduce(0, Integer::sum);
    }

    public Integer getCountType() {
        return items == null ? 0 : items.size();
    }

    public BigDecimal getTotalAmount() {
        return items == null ? BigDecimal.ZERO : items.stream().map(CartItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
