
package com.ecom.product.vo;

import com.ecom.common.to.MemberPrice;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class Sku {

    @JsonProperty("attr")
    private List<Attr> attr;
    @JsonProperty("skuName")
    private String skuName;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("skuTitle")
    private String skuTitle;
    @JsonProperty("skuSubtitle")
    private String skuSubtitle;
    @JsonProperty("images")
    private List<Image> images;
    @JsonProperty("descar")
    private List<String> descar;
    @JsonProperty("fullCount")
    private Integer fullCount;
    @JsonProperty("discount")
    private BigDecimal discount;
    @JsonProperty("countStatus")
    private Integer countStatus;
    @JsonProperty("fullPrice")
    private BigDecimal fullPrice;
    @JsonProperty("reducePrice")
    private BigDecimal reducePrice;
    @JsonProperty("priceStatus")
    private BigDecimal priceStatus;
    @JsonProperty("memberPrice")
    private List<MemberPrice> memberPrice;

}
