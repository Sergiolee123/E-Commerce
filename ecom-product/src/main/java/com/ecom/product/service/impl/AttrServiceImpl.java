package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.constant.ProductConstant;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.AttrAttrgroupRelationDao;
import com.ecom.product.dao.AttrDao;
import com.ecom.product.dao.AttrGroupDao;
import com.ecom.product.dao.CategoryDao;
import com.ecom.product.entity.AttrAttrgroupRelationEntity;
import com.ecom.product.entity.AttrEntity;
import com.ecom.product.entity.AttrGroupEntity;
import com.ecom.product.entity.CategoryEntity;
import com.ecom.product.service.AttrAttrgroupRelationService;
import com.ecom.product.service.AttrService;
import com.ecom.product.service.CategoryService;
import com.ecom.product.vo.AttrGroupRelationVo;
import com.ecom.product.vo.AttrRespVo;
import com.ecom.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

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

        if(Objects.equals(attr.getAttrType(), ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())){
            AttrAttrgroupRelationEntity attrAttrgroupRelation = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelation.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelation.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelation);
        }
    }

    @Transactional
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type",
                        "base".equalsIgnoreCase(type)? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                                : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());


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

        List<AttrRespVo> attrRespVos = getAttrRespVos(records);


        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Transactional
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attr = this.getById(attrId);
        List<AttrRespVo> attrRespVos = getAttrRespVos(Collections.singletonList(attr));
        for(AttrRespVo respVo:attrRespVos){
            respVo.setCatelogPath(categoryService.findCatelogPath(respVo.getCatelogId()));
        }
        return attrRespVos.get(0);
    }

    @Transactional
    public List<AttrRespVo> getAttrRespVos(List<AttrEntity> records) {

        if(records.isEmpty()){
            return new ArrayList<>();
        }

        Map<Long, AttrAttrgroupRelationEntity> attrIdMap = attrAttrgroupRelationService
                .getAttrIdMap(records.stream().map(AttrEntity::getAttrId).collect(Collectors.toSet()));

        final Map<Long, AttrGroupEntity> attrGroupIdMap = new HashMap<>();

        if(!attrIdMap.isEmpty()){
            attrGroupIdMap.putAll( attrGroupDao.getAttrGroupIdMap(attrIdMap.values().stream()
                    .map(AttrAttrgroupRelationEntity::getAttrGroupId).collect(Collectors.toSet())));
        }


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
        return attrRespVos;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        AttrAttrgroupRelationEntity attrAttrgroupRelation = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelation.setAttrGroupId(attr.getAttrGroupId());
        attrAttrgroupRelation.setAttrId(attrEntity.getAttrId());
        Long count = attrAttrgroupRelationDao.selectCount(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
        if(count > 0){
            attrAttrgroupRelationDao.update(new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
        } else {
            attrAttrgroupRelationService.save(attrAttrgroupRelation);
        }

    }

    /**
     * Return all base attributes related to a specified attrgroupId
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));
        return attrAttrgroupRelationEntities.isEmpty() ?new ArrayList<>() : this.listByIds(attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList()));
    }

    @Override
    public void deleteRelation(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> collect = vos.stream().map(v -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(v, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(collect);
    }

    /**
     * Return all base attributes with in a group which are not yet related to any group
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, catelogId));

        List<Long> attrGroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));

        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        LambdaQueryWrapper<AttrEntity> attrEntityLambdaQueryWrapper = new LambdaQueryWrapper<AttrEntity>().eq(AttrEntity::getCatelogId, catelogId).eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

        if(!attrIds.isEmpty()){
            attrEntityLambdaQueryWrapper.notIn(AttrEntity::getAttrId, attrIds);
        }

        String key = (String) params.get("key");
        if(StringUtils.isNotBlank(key)){
            attrEntityLambdaQueryWrapper.and((w)-> {
                w.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityLambdaQueryWrapper);

        return new PageUtils(page);

    }

}