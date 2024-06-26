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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;



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

}