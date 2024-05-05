package com.ecom.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecom.product.entity.AttrGroupEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Map;

/**
 * 属性分组
 * 
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 21:09:17
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    @MapKey("attrGroupId")
    Map<Long, AttrGroupEntity> getAttrGroupIdMap(Collection<Long> attrGroupIds);
}
