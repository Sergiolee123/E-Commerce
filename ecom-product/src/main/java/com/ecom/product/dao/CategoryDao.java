package com.ecom.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecom.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Map;

/**
 * 商品三级分类
 * 
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 21:09:17
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    @MapKey("catId")
    Map<Long, CategoryEntity> getCatIdMap(Collection<Long> catIds);

}
