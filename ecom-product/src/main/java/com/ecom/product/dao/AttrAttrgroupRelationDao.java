package com.ecom.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecom.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
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
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    @MapKey("attrId")
    Map<Long, AttrAttrgroupRelationEntity> getAttrIdMap(@Param("attrIds") Collection<Long> attrIds);

}
