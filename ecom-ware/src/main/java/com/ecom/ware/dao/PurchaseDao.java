package com.ecom.ware.dao;

import com.ecom.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 22:50:13
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
