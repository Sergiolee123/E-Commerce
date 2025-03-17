package com.ecom.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {
    private List<MemberAddressVo> address;
    private List<OrderItemVo> items;
    private Integer integration;
    private BigDecimal total;
    private BigDecimal payPrice;
    private String orderToken;
}
