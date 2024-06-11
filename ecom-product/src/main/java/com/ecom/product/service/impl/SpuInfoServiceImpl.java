package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.to.SkuReductionTo;
import com.ecom.common.to.SpuBoundTo;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.common.utils.R;
import com.ecom.product.dao.SpuInfoDao;
import com.ecom.product.entity.*;
import com.ecom.product.feign.CouponFeignService;
import com.ecom.product.service.*;
import com.ecom.product.vo.Attr;
import com.ecom.product.vo.BaseAttr;
import com.ecom.product.vo.Sku;
import com.ecom.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public SpuInfoServiceImpl(SpuInfoDescService infoDescService, SpuImagesService imagesService, ProductAttrValueService attrValueService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService, CouponFeignService couponFeignService) {
        this.infoDescService = infoDescService;
        this.imagesService = imagesService;
        this.attrValueService = attrValueService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.couponFeignService = couponFeignService;
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
            skuInfoEntity.setSkuDefaultImg(item.getImages().stream().filter(img -> img.getDefaultImg() == 1).findFirst().orElseThrow(NullPointerException::new).getImgUrl());

            skuInfoService.save(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            List<SkuImagesEntity> collect = item.getImages().stream()
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


}