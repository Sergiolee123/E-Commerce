package com.ecom.search.service;

import com.ecom.common.to.es.SkuEsModel;

import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels);
}
