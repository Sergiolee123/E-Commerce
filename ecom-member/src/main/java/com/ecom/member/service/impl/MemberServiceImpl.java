package com.ecom.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.exception.BizCodeEnum;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.common.utils.R;
import com.ecom.member.dao.MemberDao;
import com.ecom.member.entity.MemberEntity;
import com.ecom.member.entity.MemberLevelEntity;
import com.ecom.member.service.MemberLevelService;
import com.ecom.member.service.MemberService;
import com.ecom.member.vo.MemberLoginVo;
import com.ecom.member.vo.MemberRegistryVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    private final MemberLevelService memberLevelService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public MemberServiceImpl(MemberLevelService memberLevelService) {
        this.memberLevelService = memberLevelService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R registry(MemberRegistryVo memberRegistryVo) {
        try {
            MemberEntity memberEntity = new MemberEntity();

            MemberLevelEntity defaultLevel = memberLevelService.getOne(new LambdaQueryWrapper<MemberLevelEntity>().eq(MemberLevelEntity::getDefaultStatus, 1));

            memberEntity.setLevelId(defaultLevel.getId());

            long count = this.count(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, memberRegistryVo.getUsername()));
            if (count > 0) {
                return R.error(BizCodeEnum.VALID_EXCEPTION);
            }
            memberEntity.setUsername(memberRegistryVo.getUsername());

            count = this.count(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getMobile, memberRegistryVo.getPhone()));
            if (count > 0) {
                return R.error(BizCodeEnum.VALID_EXCEPTION);
            }
            memberEntity.setMobile(memberRegistryVo.getPhone());

            memberEntity.setPassword(bCryptPasswordEncoder.encode(memberRegistryVo.getPassword()));

            memberEntity.setEmail(memberRegistryVo.getEmail());
            this.save(memberEntity);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
        return R.ok();
    }

    @Override
    public R login(MemberLoginVo memberLoginVo) {
        MemberEntity user = this.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, memberLoginVo.getUsername()).or().eq(MemberEntity::getMobile, memberLoginVo.getUsername()));
        if (user == null) {
            return R.error(BizCodeEnum.VALID_EXCEPTION);
        }

        if (!bCryptPasswordEncoder.matches(memberLoginVo.getPassword(), user.getPassword())) {
            return R.error(BizCodeEnum.VALID_EXCEPTION);
        }

        return R.ok();
    }

}