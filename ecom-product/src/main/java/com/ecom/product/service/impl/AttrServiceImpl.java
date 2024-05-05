package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.AttrDao;
import com.ecom.product.dao.AttrGroupDao;
import com.ecom.product.dao.CategoryDao;
import com.ecom.product.entity.AttrAttrgroupRelationEntity;
import com.ecom.product.entity.AttrEntity;
import com.ecom.product.entity.AttrGroupEntity;
import com.ecom.product.entity.CategoryEntity;
import com.ecom.product.service.AttrAttrgroupRelationService;
import com.ecom.product.service.AttrService;
import com.ecom.product.vo.AttrRespVo;
import com.ecom.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);

        AttrAttrgroupRelationEntity attrAttrgroupRelation = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelation.setAttrGroupId(attr.getAttrGroupId());
        attrAttrgroupRelation.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationService.save(attrAttrgroupRelation);
    }

    @Transactional
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();


        if(catelogId != 0){
            attrEntityQueryWrapper.eq("catelog_id", catelogId);
        }

        String key = (String) params.get("key");

        if(StringUtils.isNotBlank(key)){
            attrEntityQueryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );

        List<AttrEntity> records = page.getRecords();

        Map<Long, AttrAttrgroupRelationEntity> attrIdMap = attrAttrgroupRelationService
                .getAttrIdMap(records.stream().map(AttrEntity::getAttrId).collect(Collectors.toSet()));

        Map<Long, AttrGroupEntity> attrGroupIdMap = attrGroupDao.getAttrGroupIdMap(attrIdMap.values().stream()
                .map(AttrAttrgroupRelationEntity::getAttrGroupId).collect(Collectors.toSet()));


        Map<Long, CategoryEntity> catIdMap = categoryDao
                .getCatIdMap(records.stream().map(AttrEntity::getCatelogId).collect(Collectors.toSet()));

        List<AttrRespVo> attrRespVos = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            AttrAttrgroupRelationEntity attrAttrgroupRelation = attrIdMap.get(attrEntity.getAttrId());
            if (attrAttrgroupRelation != null && attrAttrgroupRelation.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupIdMap.get(attrAttrgroupRelation.getAttrGroupId());
                if(attrGroupEntity != null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            if (attrEntity.getCatelogId() != null) {
                CategoryEntity categoryEntity = catIdMap.get(attrEntity.getCatelogId());
                if(categoryEntity != null){
                    attrRespVo.setCatelogName(categoryEntity.getName());
                }
            }
            return attrRespVo;
        }).collect(Collectors.toList());


        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

}