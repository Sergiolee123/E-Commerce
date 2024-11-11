package com.ecom.search.vo;

import com.ecom.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    private List<SkuEsModel> products;
    private int pageNum;
    private Long total;
    private Integer totalPages;
    private List<BrandVo> brands;
    private List<CatalogVo> catalogs;
    private List<AttrVo> attrs;

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}
