package com.ecom.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.ware.dao.WareInfoDao;
import com.ecom.ware.entity.WareInfoEntity;
import com.ecom.ware.service.WareInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                Wrappers.lambdaQuery(WareInfoEntity.class)
                        .nested(StringUtils.isNotBlank(key),
                                wq -> wq.eq(WareInfoEntity::getId, key).or()
                                        .like(WareInfoEntity::getName, key)
                                        .or()
                                        .like(WareInfoEntity::getAddress, key)
                                        .or()
                                        .like(WareInfoEntity::getAreacode, key)
                        )
        );

        return new PageUtils(page);
    }

}