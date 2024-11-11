package com.ecom.search.service.Impl;

import com.ecom.search.config.ElasticSearchConfig;
import com.ecom.search.constant.EsConstant;
import com.ecom.search.service.MallSearchService;
import com.ecom.search.vo.SearchParam;
import com.ecom.search.vo.SearchResult;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

        if(!CollectionUtils.isEmpty(searchParam.getAttrs())) {
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

        // aggregation

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    private SearchResult buildSearchResult(SearchResponse response) {
        return null;
    }

}
