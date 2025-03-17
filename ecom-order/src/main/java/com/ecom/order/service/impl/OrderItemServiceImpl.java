package com.ecom.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.Query;
import com.ecom.order.dao.OrderItemDao;
import com.ecom.order.entity.OrderItemEntity;
import com.ecom.order.entity.OrderReturnReasonEntity;
import com.ecom.order.service.OrderItemService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @RabbitListener(queues = {"hello-java-queue"})
    public void receiveMessage(Message message, OrderReturnReasonEntity entity, Channel channel) {
        log.info("{} \n {}", message , entity);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex){

            }
            throw new RuntimeException(e);
        }
    }

}