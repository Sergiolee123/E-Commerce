package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.SkuInfoDao;
import com.ecom.product.entity.SkuImagesEntity;
import com.ecom.product.entity.SkuInfoEntity;
import com.ecom.product.entity.SpuInfoDescEntity;
import com.ecom.product.service.*;
import com.ecom.product.vo.SkuItemAttrGroupVo;
import com.ecom.product.vo.SkuItemSaleAttrVo;
import com.ecom.product.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    private final SkuImagesService skuImagesService;
    private final SpuInfoDescService spuInfoDescService;
    private final AttrGroupService attrGroupService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final ThreadPoolExecutor threadPoolExecutor;

    public SkuInfoServiceImpl(SkuImagesService skuImagesService, SpuInfoDescService spuInfoDescService, AttrGroupService attrGroupService, SkuSaleAttrValueService skuSaleAttrValueService, ThreadPoolExecutor threadPoolExecutor) {
        this.skuImagesService = skuImagesService;
        this.spuInfoDescService = spuInfoDescService;
        this.attrGroupService = attrGroupService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.threadPoolExecutor = threadPoolExecutor;
    }

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

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // sku basic info
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((info) -> {
            // spu sales attrs
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(info.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> spuInfoDescFuture = infoFuture.thenAcceptAsync(info -> {
            // sku desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(info.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, threadPoolExecutor);

        CompletableFuture<Void> attrGroupFuture = infoFuture.thenAcceptAsync(info -> {
            //spu base attrs
            List<SkuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(info.getSpuId(), info.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, threadPoolExecutor);

        // sku image info
        List<SkuImagesEntity> skuImagesEntities = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(skuImagesEntities);

        CompletableFuture.allOf(saleAttrFuture, spuInfoDescFuture, attrGroupFuture).join();

        return skuItemVo;
    }

}