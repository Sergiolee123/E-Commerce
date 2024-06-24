package com.ecom.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.ware.dao.PurchaseDetailDao;
import com.ecom.ware.entity.PurchaseDetailEntity;
import com.ecom.ware.service.PurchaseDetailService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                Wrappers.lambdaQuery(PurchaseDetailEntity.class)
                        .and(StringUtils.isNotBlank(key), wq
                                -> wq.eq(PurchaseDetailEntity::getPurchaseId, key)
                                .or()
                                .eq(PurchaseDetailEntity::getSkuId, key)
                        )
                        .eq(StringUtils.isNotBlank(status), PurchaseDetailEntity::getStatus, status)
                        .eq(StringUtils.isNotBlank(wareId), PurchaseDetailEntity::getWareId, wareId)
        );

        return new PageUtils(page);
    }

}