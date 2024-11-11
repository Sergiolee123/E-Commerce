package com.ecom.search.service.Impl;

import com.ecom.search.config.ElasticSearchConfig;
import com.ecom.search.constant.EsConstant;
import com.ecom.search.service.MallSearchService;
import com.ecom.search.vo.SearchParam;
import com.ecom.search.vo.SearchResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    private final RestHighLevelClient esClient;

    public MallSearchServiceImpl(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    @Override
    @SneakyThrows
    public SearchResult search(SearchParam searchParam) {
        SearchRequest searchRequest = buildSearchRequest(searchParam);

        SearchResponse response = esClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        return buildSearchResult(response);
    }

    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // fuzz search, filter
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            query.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }

        if (searchParam.getCatalog3Id() != null) {
            query.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }

        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            query.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }

        if (!CollectionUtils.isEmpty(searchParam.getAttrs())) {
            for (String attrStr : searchParam.getAttrs()) {
                BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();
                String[] attrArr = attrStr.split("_");
                String attrId = attrArr[0];
                String[] attrValues = attrArr[1].split(":");
                nestBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestBoolQuery, ScoreMode.None);
                query.filter(nestedQuery);
            }
        }

        if (searchParam.getHasStock() != null) {
            query.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }

        if (StringUtils.isNotEmpty(searchParam.getSkuPrice())) {
            String[] inputs = searchParam.getSkuPrice().split("_");
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            if (inputs.length == 2 && StringUtils.isNotEmpty(inputs[0])) {
                rangeQuery.gte(inputs[0]).lte(inputs[1]);
            } else if (inputs.length == 2) {
                rangeQuery.lte(inputs[1]);
            } else if (inputs.length == 1) {
                rangeQuery.gte(inputs[0]);
            }
            query.filter(rangeQuery);

        }
        sourceBuilder.query(query);

        // sorting, paging, highlight
        if (StringUtils.isNotEmpty(searchParam.getSort())) {
            String[] sortArgs = searchParam.getSort().split("_");
            sourceBuilder.sort(sortArgs[0], "asc".equalsIgnoreCase(sortArgs[1]) ? SortOrder.ASC : SortOrder.DESC);
        }

        if (searchParam.getPageNum() != null) {
            sourceBuilder.from((searchParam.getPageNum() * EsConstant.PRODUCT_PAGE_SIZE) - EsConstant.PRODUCT_PAGE_SIZE);
            sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        } else {
            sourceBuilder.from(0);
            sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        }

        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }


        // aggregation
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId");
        brandAgg.size(50);
        TermsAggregationBuilder brandNameAgg = AggregationBuilders.terms("brand_name_agg");
        brandNameAgg.field("brandName");
        brandNameAgg.size(1);
        brandAgg.subAggregation(brandNameAgg);
        TermsAggregationBuilder brandImgAgg = AggregationBuilders.terms("brand_img_agg");
        brandImgAgg.field("brandImg");
        brandImgAgg.size(1);
        brandAgg.subAggregation(brandImgAgg);
        sourceBuilder.aggregation(brandAgg);

        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId");
        catalogAgg.size(50);
        TermsAggregationBuilder catalogNameAgg = AggregationBuilders.terms("catalog_name_agg");
        catalogNameAgg.field("catalogName");
        catalogNameAgg.size(1);
        catalogAgg.subAggregation(catalogNameAgg);
        sourceBuilder.aggregation(catalogAgg);

        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50);
        attrIdAgg.subAggregation(attrValueAgg);
        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);

        log.info(sourceBuilder.toString());

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    private SearchResult buildSearchResult(SearchResponse response) {
        return null;
    }

}
