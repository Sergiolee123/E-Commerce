
package com.ecom.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseAttr {

    @JsonProperty("attrId")
    private Long attrId;
    @JsonProperty("attrValues")
    private String attrValues;
    @JsonProperty("showDesc")
    private Integer showDesc;

}
