package com.ecom.search.controller;

import com.ecom.common.exception.BizCodeEnum;
import com.ecom.common.to.es.SkuEsModel;
import com.ecom.common.utils.R;
import com.ecom.search.service.ProductSaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    private final ProductSaveService productSaveService;

    public ElasticSaveController(ProductSaveService productSaveService) {
        this.productSaveService = productSaveService;
    }

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

        try {
            productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
        }

        return R.ok();
    }


}
