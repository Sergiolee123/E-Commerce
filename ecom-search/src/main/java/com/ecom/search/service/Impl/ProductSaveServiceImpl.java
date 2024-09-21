package com.ecom.search.service.Impl;

import com.ecom.common.to.es.SkuEsModel;
import com.ecom.search.config.ElasticSearchConfig;
import com.ecom.search.constant.EsConstant;
import com.ecom.search.service.ProductSaveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper;

    public ProductSaveServiceImpl(RestHighLevelClient esClient, ObjectMapper objectMapper) {
        this.esClient = esClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @SneakyThrows
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) {

        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            indexRequest.source(objectMapper.writeValueAsString(skuEsModel), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = esClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);


        if(bulk.hasFailures()) {
            log.error(bulk.buildFailureMessage());
            throw new RuntimeException();
        }

        return bulk.hasFailures();
    }
}
