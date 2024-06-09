package com.ecom.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVo {
    @JsonProperty("spuName")
    private String spuName;
    @JsonProperty("spuDescription")
    private String spuDescription;
    @JsonProperty("catalogId")
    private Integer catalogId;
    @JsonProperty("brandId")
    private Integer brandId;
    @JsonProperty("weight")
    private BigDecimal weight;
    @JsonProperty("publishStatus")
    private Integer publishStatus;
    @JsonProperty("decript")
    private List<String> decript;
    @JsonProperty("images")
    private List<String> images;
    @JsonProperty("bounds")
    private Bounds bounds;
    @JsonProperty("baseAttrs")
    private List<BaseAttr> baseAttrs;
    @JsonProperty("skus")
    private List<Sku> skus;

}
