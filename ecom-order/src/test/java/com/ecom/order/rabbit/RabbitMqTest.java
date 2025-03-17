package com.ecom.order.rabbit;

import com.ecom.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@Slf4j
@SpringBootTest
public class RabbitMqTest {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCreateExchange() {
        amqpAdmin.declareExchange(new DirectExchange("hello-java-exchange", true, false));
    }

    @Test
    public void testCreateQueue() {
        amqpAdmin.declareQueue(new Queue("hello-java-queue", true, false, false));
    }

    @Test
    public void testCreateBinding() {
        amqpAdmin.declareBinding(new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null));
    }

    @Test
    public void testSendMessage() {
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", new OrderReturnReasonEntity(), new CorrelationData(UUID.randomUUID().toString()));
    }

    @Test
    public void testReceiveMessage() {
        Message receive = rabbitTemplate.receive("hello-java-queue");
        if (receive != null) {
            log.info(receive.toString());
        }
    }
}
