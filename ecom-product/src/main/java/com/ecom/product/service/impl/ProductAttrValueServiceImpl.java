package com.ecom.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.product.dao.ProductAttrValueDao;
import com.ecom.product.entity.ProductAttrValueEntity;
import com.ecom.product.service.ProductAttrValueService;
import com.ecom.product.vo.BaseAttr;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    private final AttrServiceImpl attrService;

    public ProductAttrValueServiceImpl(AttrServiceImpl attrService) {
        this.attrService = attrService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttrs(Long id, List<BaseAttr> baseAttrs) {
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(attr.getAttrId());
            attrValueEntity.setAttrName(attrService.getById(attr.getAttrId()).getAttrName());
            attrValueEntity.setAttrValue(attr.getAttrValues());
            attrValueEntity.setQuickShow(attr.getShowDesc());
            attrValueEntity.setSpuId(id);

            return attrValueEntity;
        }).collect(Collectors.toList());

        this.saveBatch(collect);
    }

}