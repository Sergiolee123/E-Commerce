package com.ecom.product.vo;

import com.ecom.product.entity.SkuImagesEntity;
import com.ecom.product.entity.SkuInfoEntity;
import com.ecom.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    SkuInfoEntity info;
    List<SkuImagesEntity> images;
    List<SkuItemSaleAttrVo> saleAttrs;
    SpuInfoDescEntity desp;
    List<SkuItemAttrGroupVo> groupAttrs;
}
