package com.ecom.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecom.common.utils.PageUtils;
import com.ecom.product.entity.AttrEntity;
import com.ecom.product.vo.AttrGroupRelationVo;
import com.ecom.product.vo.AttrRespVo;
import com.ecom.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 21:09:17
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(List<AttrGroupRelationVo> vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    List<Long> selectSearchAttrIds(List<Long> collectAttrId);
}

