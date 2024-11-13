package com.ecom.search.service.Impl;

import com.ecom.common.exception.BizCodeEnum;
import com.ecom.common.exception.RRException;
import com.ecom.common.to.es.SkuEsModel;
import com.ecom.search.config.ElasticSearchConfig;
import com.ecom.search.constant.EsConstant;
import com.ecom.search.service.MallSearchService;
import com.ecom.search.vo.SearchParam;
import com.ecom.search.vo.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper;

    public MallSearchServiceImpl(RestHighLevelClient esClient, ObjectMapper objectMapper) {
        this.esClient = esClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @SneakyThrows
    public SearchResult search(SearchParam searchParam) {
        SearchRequest searchRequest = buildSearchRequest(searchParam);

        SearchResponse response = esClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        return buildSearchResult(response, searchParam);
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
            searchParam.setPageNum(1);
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

    @SneakyThrows
    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        SearchHits hits = response.getHits();
        if(hits == null) {
            throw new RRException(BizCodeEnum.RESULT_NOT_FOUND_EXCEPTION);
        }

        SearchResult searchResult = new SearchResult();

        List<SkuEsModel> esModels = new ArrayList<>();
        if(hits.getHits() != null) {
            for (SearchHit hit : hits.getHits()) {
                SkuEsModel esModel = objectMapper.readValue(hit.getSourceAsString(), SkuEsModel.class);
                if(StringUtils.isNotEmpty(searchParam.getKeyword())) {
                    esModel.setSkuTitle(hit.getHighlightFields().get("skuTitle").getFragments()[0].string());
                }
                esModels.add(esModel);
            }
        }
        searchResult.setProducts(esModels);

        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>(catalogAgg.getBuckets().size());
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(bucket.getKeyAsNumber().longValue());
            Aggregations catalogInfoAgg = bucket.getAggregations();
            ParsedStringTerms catalogNameAgg = catalogInfoAgg.get("catalog_name_agg");
            catalogVo.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);

        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        List<SearchResult.BrandVo> brandVos = new ArrayList<>(brandAgg.getBuckets().size());
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
            Aggregations brandInfoAgg = bucket.getAggregations();
            ParsedStringTerms brandNameAggName = brandInfoAgg.get("brand_name_agg");
            brandVo.setBrandName(brandNameAggName.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms brandImgAgg = brandInfoAgg.get("brand_img_agg");
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);

        ParsedNested attrAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrVo> attrVos = new ArrayList<>(attrIdAgg.getBuckets().size());
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            Aggregations attrInfoAgg = bucket.getAggregations();
            ParsedStringTerms attrNameAgg = attrInfoAgg.get("attr_name_agg");
            attrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms attrValueAgg = attrInfoAgg.get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);

        // paging info
        searchResult.setPageNum(searchParam.getPageNum());
        long total = Optional.ofNullable(hits.getTotalHits())
                .orElseThrow(()-> new RRException(BizCodeEnum.RESULT_NOT_FOUND_EXCEPTION))
                .value;
        searchResult.setTotal(total);
        searchResult.setTotalPages((int) Math.ceil((double) total/EsConstant.PRODUCT_PAGE_SIZE));

        return searchResult;
    }

}
