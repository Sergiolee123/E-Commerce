package com.ecom.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecom.common.utils.PageUtils;
import com.ecom.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 21:09:17
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<Long, AttrAttrgroupRelationEntity> getAttrIdMap(@Param("attrIds") Collection<Long> attrIds);
}

