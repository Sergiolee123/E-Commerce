package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.CategoryDao;
import com.ecom.product.entity.CategoryEntity;
import com.ecom.product.service.CategoryBrandRelationService;
import com.ecom.product.service.CategoryService;
import com.ecom.product.vo.Catelog2Vo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationService categoryBrandRelationService;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.categoryBrandRelationService = categoryBrandRelationService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                .orderByAsc("parent_cid", "cat_level", "cat_id"));
        Map<Long, CategoryEntity> groupMap = new LinkedHashMap<>();
        List<CategoryEntity> results = new ArrayList<>();
        //use hashmap to combine parent and sub menus
        for (CategoryEntity ce : entities) {

            groupMap.put(ce.getCatId(), ce); // add menus to map, use catId as key for grouping sub menus

            if (ce.getParentCid() == 0) { //skip grouping for parent menus
                results.add(ce);
                continue;
            }

            groupMap.merge(ce.getParentCid(), ce, (c1, c2) -> {
                c1.addChildren(c2); //add sub menus to parent menus where sub menus parentCid = parent menus catId
                return c1;
            });
        }
        // sort the main menus by sorting field
        return results
                .stream()
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> ids) {

        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();

        CategoryEntity byId = this.getById(catelogId);
        paths.add(byId.getCatId());
        do {
            byId = this.getById(byId.getParentCid());
            paths.add(byId.getCatId());
        } while (byId.getParentCid() != 0);

        Collections.reverse(paths);

        return paths.toArray(new Long[0]);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", "1"));
    }

    @Override
    @SneakyThrows
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> catalogJson;
        String json = redisTemplate.opsForValue().get("catalogJson");
        if(StringUtils.isEmpty(json)) {
            catalogJson = getCatalogJsonFromDb();
        } else {
            catalogJson = objectMapper.readValue(json, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
        }
        return catalogJson;
    }


    @SneakyThrows
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

        String json = redisTemplate.opsForValue().get("catalogJson");
        if(StringUtils.isNotEmpty(json)) {
            return objectMapper.readValue(json, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
        }

        List<CategoryEntity> level1Categories = getLevel1Categories();

        Map<String, List<Catelog2Vo>> catalogJson = level1Categories.stream()
                .collect(Collectors.toMap(
                        k -> k.getCatId().toString(),
                        v -> {
                            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                                    .eq("parent_cid", v.getCatId()));

                            return categoryEntities.stream().map(item -> {
                                Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());

                                List<CategoryEntity> categoryEntities3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                                        .eq("parent_cid", item.getCatId()));

                                catelog2Vo.setCatalog3List(
                                        categoryEntities3.stream().map(item3 -> new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), item3.getCatId().toString(), item3.getName()))
                                                .collect(Collectors.toList())
                                );

                                return catelog2Vo;

                            }).collect(Collectors.toList());

                        }
                ));

        redisTemplate.opsForValue().set("catalogJson", objectMapper.writeValueAsString(catalogJson), 1, TimeUnit.DAYS);

        return catalogJson;

    }

}