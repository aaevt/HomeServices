package com.sop.smarthome.smart_home_core.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "smartHomeQueue";
    public static final String EXCHANGE_NAME = "smartHomeExchange";
    public static final String ROUTING_key = "my_key";

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-message-ttl", 60000)
                .build();
    }

    @Bean
    Exchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, false, false);
    }

    @Bean
    Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_key).noargs();
    }
}
