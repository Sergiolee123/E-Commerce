package com.ecom.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.R;
import com.ecom.member.entity.MemberEntity;
import com.ecom.member.vo.MemberLoginVo;
import com.ecom.member.vo.MemberRegistryVo;

import java.util.Map;

/**
 * 会员
 *
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 22:46:00
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R registry(MemberRegistryVo memberRegistryVo);

    R login(MemberLoginVo memberLoginVo);
}

