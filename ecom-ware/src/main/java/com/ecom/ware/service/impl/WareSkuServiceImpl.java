package com.ecom.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.common.utils.R;
import com.ecom.ware.dao.WareSkuDao;
import com.ecom.ware.entity.WareSkuEntity;
import com.ecom.ware.feign.ProductFeignService;
import com.ecom.ware.service.WareSkuService;
import com.ecom.ware.vo.SkuHasStockVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    private final ProductFeignService productFeignService;
    private final WareSkuDao wareSkuDao;

    public WareSkuServiceImpl(ProductFeignService productFeignService, WareSkuDao wareSkuDao) {
        this.productFeignService = productFeignService;
        this.wareSkuDao = wareSkuDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                Wrappers.lambdaQuery(WareSkuEntity.class)
                        .eq(StringUtils.isNotBlank(skuId), WareSkuEntity::getSkuId, skuId)
                        .eq(StringUtils.isNotBlank(wareId), WareSkuEntity::getWareId, wareId)
        );

        return new PageUtils(page);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        long count = this.baseMapper.selectCount(new LambdaQueryWrapper<WareSkuEntity>()
                .eq(WareSkuEntity::getSkuId, skuId)
                .eq(WareSkuEntity::getWareId, wareId));

        if(count == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            try {
                R info = productFeignService.info(skuId);
                if(info.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            wareSkuEntity.setStockLocked(0);
            this.baseMapper.insert(wareSkuEntity);
        } else {
            this.update(new LambdaUpdateWrapper<WareSkuEntity>()
                    .setSql("stock=stock+{0}", skuId)
                    .eq(WareSkuEntity::getSkuId, skuId)
                    .eq(WareSkuEntity::getWareId, wareId));
        }
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();

            long stock = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(stock > 0);
            return vo;
        }).collect(Collectors.toList());
    }

}