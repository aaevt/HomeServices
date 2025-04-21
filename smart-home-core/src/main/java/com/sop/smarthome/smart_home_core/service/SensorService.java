package com.sop.smarthome.smart_home_core.service;

import com.sop.smarthome.smart_home_core.rabbitmq.RabbitMQConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.sop.smarthome.smart_home_core.models.Sensor;
import com.sop.smarthome.smart_home_core.repositories.SensorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    @PostConstruct
    public void init() {
        startSpammingForAllActiveSensors();
    }

    public SensorService(SensorRepository sensorRepository, RabbitTemplate rabbitTemplate) {
        this.sensorRepository = sensorRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    public Optional<Sensor> getSensorById(String id) {
        return sensorRepository.findById(id);
    }

    public Sensor createSensor(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public void deleteSensor(String id) {
        sensorRepository.deleteById(id);
    }

    public void updateSensorStatus(Sensor sensor) {
        if (sensor.getStatus()) {
            startSensorSpamming(sensor);
        } else {
            stopSensorSpamming(sensor);
        }
    }

    public void stopSensorSpamming(Sensor sensor) {
        System.out.println("Stopped spamming data for sensor ID: " + sensor.getId());
    }

    public void startSpammingForAllActiveSensors() {
        List<Sensor> allSensors = sensorRepository.findAll();
        for (Sensor sensor : allSensors) {
            if (sensor.getStatus()) {
                startSensorSpamming(sensor);
            }
        }
    }

    public void startSensorSpamming(Sensor sensor) {
        Random random = new Random();
        executorService.scheduleAtFixedRate(() -> {
            if (sensor.getStatus()) {
                double randomData = -10 + (40 - (-10)) * random.nextDouble();
                sensor.setData(randomData);

                sensorRepository.save(sensor);

                String message = String.format("Sensor ID: %s, Data: %f", sensor.getId(), sensor.getData());

                if (sensor.getData() > 20) {
                    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "not_my_key", message);
                    System.out.println("Sent message with high_data_key: " + message);
                } else {
                    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "my_key", message);
                    System.out.println("Sent message with low_data_key: " + message);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
