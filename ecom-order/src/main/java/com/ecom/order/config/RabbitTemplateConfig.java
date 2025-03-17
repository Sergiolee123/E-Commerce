package com.ecom.order.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RabbitTemplateConfig {

    private final RabbitTemplate rabbitTemplate;

    public RabbitTemplateConfig(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {
            System.out.println("ack: " + ack);
        });

        rabbitTemplate.setReturnsCallback((returned) -> {
            System.out.println("replyCode: " + returned.getReplyCode());
        });
    }
}
