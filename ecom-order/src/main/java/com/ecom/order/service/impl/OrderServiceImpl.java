package com.ecom.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.order.constants.OrderConstant;
import com.ecom.order.dao.OrderDao;
import com.ecom.order.entity.OrderEntity;
import com.ecom.order.feign.MemberFeignService;
import com.ecom.order.service.OrderService;
import com.ecom.order.vo.MemberAddressVo;
import com.ecom.order.vo.OrderConfirmVo;
import com.ecom.order.vo.OrderSubmitVo;
import com.ecom.order.vo.SubmitOrderResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private final MemberFeignService memberFeignService;
    private final StringRedisTemplate redisTemplate;

    public OrderServiceImpl(MemberFeignService memberFeignService, StringRedisTemplate redisTemplate) {
        this.memberFeignService = memberFeignService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo comfirmOrder() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        List<MemberAddressVo> memberAddressVos = memberFeignService.listByMemberId(1L);
        confirmVo.setAddress(memberAddressVos);

        // todo: call cart service
        confirmVo.setItems(new ArrayList<>());

        String token = UUID.randomUUID().toString().replace("-", "");
        confirmVo.setOrderToken(token);
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + "1", token, 30, TimeUnit.MINUTES);

        return confirmVo;
    }

    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {

        String token = orderSubmitVo.getOrderToken();
        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + "1");
        if(StringUtils.equals(token, redisToken)) {

        }

        return null;
    }

}