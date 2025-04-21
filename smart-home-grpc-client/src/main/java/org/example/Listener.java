package org.example;

import com.google.protobuf.util.JsonFormat;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import smarthome.SmartHomeCore.*;

@Component
public class Listener {

    static final String queueName = "GRPCQueue";
    static final String mainQueue = "smartHomeQueue";

    @Autowired
    private Sender sender;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public Queue myQueue() {
        return new Queue(queueName, false);
    }

    @RabbitListener(queues = queueName)
    public void listen(String message) {
        System.out.println("1. Message read from " + queueName + " : " + message);

        try (QueueClient client = new QueueClient("localhost", 8088)) {
            Sensor sensor = client.getAnalyze(message);
            if (sensor != null) {
                System.out.println("\n2. Sensor data received from gRPC:\n" + sensor);

                String jsonMessage = JsonFormat.printer().print(sensor);
                rabbitTemplate.convertAndSend(mainQueue, jsonMessage);
            } else {
                System.err.println("Failed to analyze message: " + message);
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
