package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.constant.ProductConstant;
import com.ecom.common.to.SkuReductionTo;
import com.ecom.common.to.SpuBoundTo;
import com.ecom.common.to.es.SkuEsModel;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.common.utils.R;
import com.ecom.product.dao.SpuInfoDao;
import com.ecom.product.entity.*;
import com.ecom.product.feign.CouponFeignService;
import com.ecom.product.feign.SearchFeignService;
import com.ecom.product.feign.WareFeignService;
import com.ecom.product.service.*;
import com.ecom.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private final SpuInfoDescService infoDescService;
    private final SpuImagesService imagesService;
    private final ProductAttrValueService attrValueService;
    private final SkuInfoService skuInfoService;
    private final SkuImagesService skuImagesService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final CouponFeignService couponFeignService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final AttrServiceImpl attrService;
    private final WareFeignService wareFeignService;
    private final SearchFeignService searchFeignService;

    public SpuInfoServiceImpl(SpuInfoDescService infoDescService, SpuImagesService imagesService, ProductAttrValueService attrValueService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService, CouponFeignService couponFeignService, BrandService brandService, CategoryService categoryService, AttrServiceImpl attrService, WareFeignService wareFeignService, SearchFeignService searchFeignService) {
        this.infoDescService = infoDescService;
        this.imagesService = imagesService;
        this.attrValueService = attrValueService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.couponFeignService = couponFeignService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.attrService = attrService;
        this.wareFeignService = wareFeignService;
        this.searchFeignService = searchFeignService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity infoDescEntity = new SpuInfoDescEntity();
        infoDescEntity.setSpuId(spuInfoEntity.getId());
        infoDescEntity.setDecript(String.join(",", decript));
        infoDescService.save(infoDescEntity);

        List<String> images = spuSaveVo.getImages();
        imagesService.saveImages(spuInfoEntity.getId(), images);

        List<BaseAttr> baseAttrs = spuSaveVo.getBaseAttrs();
        attrValueService.saveAttrs(spuInfoEntity.getId(), baseAttrs);

        List<Sku> skus = spuSaveVo.getSkus();
        for(Sku item : skus) {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(item, skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSkuDefaultImg(item.getImages().stream().filter(img -> img.getDefaultImg() == 1).findFirst().orElse(new Image()).getImgUrl());

            skuInfoService.save(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            List<SkuImagesEntity> collect = item.getImages().stream()
                    .filter(img -> StringUtils.isNotBlank(img.getImgUrl()))
                    .map(img -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setImgUrl(img.getImgUrl());
                        skuImagesEntity.setDefaultImg(img.getDefaultImg());
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
            skuImagesService.saveBatch(collect);

            List<Attr> attrs = item.getAttr();
            List<SkuSaleAttrValueEntity> collect1 = attrs.stream().map(a -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());

            skuSaleAttrValueService.saveBatch(collect1);

            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item, skuReductionTo);
            skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
            R r = couponFeignService.saveSkuReduction(skuReductionTo);
            if(r.getCode() != 0) {
                log.error("remote sku coupon save service fail");
                throw new RuntimeException("remote sku coupon save service fail");
            }
        }

        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0) {
            log.error("remote spu coupon save service fail");
            throw new RuntimeException("remote spu coupon save service fail");
        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                Wrappers.lambdaQuery(SpuInfoEntity.class)
                        .and(StringUtils.isNotBlank(key) && !"0".equalsIgnoreCase(key), wq ->
                                wq.eq(SpuInfoEntity::getId, key).or().eq(SpuInfoEntity::getSpuName, key))
                        .eq(StringUtils.isNotBlank(status), SpuInfoEntity::getPublishStatus, status)
                        .eq(StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId), SpuInfoEntity::getBrandId, brandId)
                        .eq(StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId), SpuInfoEntity::getCatalogId, catelogId)
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void up(Long spuId) {

        List<ProductAttrValueEntity> productAttrValueEntities = attrValueService.baseAttrListForSpu(spuId);
        List<Long> collectAttrId = productAttrValueEntities.stream()
                .map(ProductAttrValueEntity::getAttrId)
                .collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrIds(collectAttrId);

        Set<Long> set = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList = productAttrValueEntities.stream()
                .filter(item -> set.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                })
                .collect(Collectors.toList());

        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);

        R skuHasStock = wareFeignService.getSkuHasStock(skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
        List<Map<String, Object>> data = (List<Map<String, Object>>) skuHasStock.get("data");
        Map<Long, Boolean> hasStockMap = new HashMap<>(data.size());
        for (Map<String, Object> item : data) {
            hasStockMap.put(((Integer) item.get("skuId")).longValue(), (Boolean) item.get("hasStock"));
        }

        List<SkuEsModel> upProducts = skuInfoEntities.stream().map(skuInfoEntity -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfoEntity, esModel);
            esModel.setSkuPrice(skuInfoEntity.getPrice());
            esModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());

            //todo: check warehouse stock
            esModel.setHasStock(hasStockMap.get(skuInfoEntity.getSkuId()));
            //todo: check hot score
            esModel.setHotScore(0L);

            BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
            esModel.setCatalogId(skuInfoEntity.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            //todo: add attrs
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());

        //todo: send to es
        R r = searchFeignService.productStatusUp(upProducts);

        if(r.getCode() == 0) {
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {

        }

    }


}