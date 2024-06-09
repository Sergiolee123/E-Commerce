
package com.ecom.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bounds {

    @JsonProperty("buyBounds")
    private BigDecimal buyBounds;
    @JsonProperty("growBounds")
    private BigDecimal growBounds;

}
