package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.SpuInfoDao;
import com.ecom.product.entity.SpuInfoDescEntity;
import com.ecom.product.entity.SpuInfoEntity;
import com.ecom.product.service.ProductAttrValueService;
import com.ecom.product.service.SpuImagesService;
import com.ecom.product.service.SpuInfoDescService;
import com.ecom.product.service.SpuInfoService;
import com.ecom.product.vo.BaseAttr;
import com.ecom.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private final SpuInfoDescService infoDescService;
    private final SpuImagesService imagesService;
    private final ProductAttrValueService attrValueService;

    public SpuInfoServiceImpl(SpuInfoDescService infoDescService, SpuImagesService imagesService, ProductAttrValueService attrValueService) {
        this.infoDescService = infoDescService;
        this.imagesService = imagesService;
        this.attrValueService = attrValueService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
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

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }


}