package com.sop.smarthome.smart_home_audit_log.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.sop.smarthome.smart_home_audit_log.rabbitmq.RabbitMQConfig.QUEUE_NAME;

@Component
public class RabbitMQListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(String message) {
        try {
        System.out.println("Message read from " + QUEUE_NAME + " : " + message);
        logger.info("Received message: {}", message);
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
        }
    }
}
