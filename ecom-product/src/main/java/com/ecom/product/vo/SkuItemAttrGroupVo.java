package com.ecom.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemAttrGroupVo {
    private String groupName;
    private List<SkuBaseAttrVo> attrs;
}
