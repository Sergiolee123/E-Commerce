
package com.ecom.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Attr {

    @JsonProperty("attrId")
    private Long attrId;
    @JsonProperty("attrName")
    private String attrName;
    @JsonProperty("attrValue")
    private String attrValue;


}
