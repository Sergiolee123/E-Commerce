package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.SkuInfoDao;
import com.ecom.product.entity.SkuInfoEntity;
import com.ecom.product.service.SkuInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                Wrappers.lambdaQuery(SkuInfoEntity.class)
                        .and(StringUtils.isNotBlank(key) && !"0".equalsIgnoreCase(key), wq ->
                                wq.eq(SkuInfoEntity::getSkuId, key).or().eq(SkuInfoEntity::getSkuName, key))
                        .eq(StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId), SkuInfoEntity::getCatalogId, catelogId)
                        .eq(StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId), SkuInfoEntity::getBrandId, brandId)
                        .ge(StringUtils.isNotBlank(min), SkuInfoEntity::getPrice, new BigDecimal(min))
                        .le(StringUtils.isNotBlank(max), SkuInfoEntity::getPrice, new BigDecimal(max))
        );

        return new PageUtils(page);
    }

}