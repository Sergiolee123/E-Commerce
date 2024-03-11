package com.ecom.member.dao;

import com.ecom.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 22:46:00
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
