package com.ecom.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AttrRespVo extends AttrVo{
    private String catelogName;
    private String groupName;
}
