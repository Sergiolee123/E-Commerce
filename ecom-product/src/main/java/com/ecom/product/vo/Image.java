
package com.ecom.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Image {

    @JsonProperty("imgUrl")
    private String imgUrl;
    @JsonProperty("defaultImg")
    private Integer defaultImg;


}
