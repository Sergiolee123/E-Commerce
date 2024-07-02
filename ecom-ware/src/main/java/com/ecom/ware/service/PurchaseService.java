package com.ecom.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecom.common.utils.PageUtils;
import com.ecom.ware.entity.PurchaseEntity;
import com.ecom.ware.vo.MergeVo;
import com.ecom.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 22:50:13
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

