package com.sop.smarthome.smart_home_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sop.smarthome.smart_home_core.models.Sensor;
import com.sop.smarthome.smart_home_core.service.SensorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @GetMapping
    public CollectionModel<EntityModel<Sensor>> getAllSensors() {
        List<Sensor> sensors = sensorService.getAllSensors();
        List<EntityModel<Sensor>> sensorModels = sensors.stream().map(sensor -> {
            EntityModel<Sensor> sensorModel = EntityModel.of(sensor);
            Link selfLink = linkTo(methodOn(SensorController.class).getSensorById(sensor.getId())).withSelfRel();
            sensorModel.add(selfLink);

            return sensorModel;
        }).toList();

        Link selfLink = linkTo(methodOn(SensorController.class).getAllSensors()).withSelfRel();
        return CollectionModel.of(sensorModels, selfLink);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSensorById(@PathVariable String id) {
        Optional<Sensor> sensorOpt = sensorService.getSensorById(id);
        if (sensorOpt.isEmpty()) {
            throw new RuntimeException("Sensor not found");
        }
        Sensor sensor = sensorOpt.get();

        Map<String, Object> links = new HashMap<>();
        links.put("self", Map.of("href", linkTo(methodOn(SensorController.class).getSensorById(id)).toString()));
        links.put("all", Map.of("href", linkTo(methodOn(SensorController.class).getAllSensors()).toString()));

        Map<String, Object> actions = new HashMap<>();
        actions.put("update", Map.of(
                "href", linkTo(methodOn(SensorController.class).updateSensor(id, null)).toString(),
                "method", "PUT",
                "accept", "application/json"
        ));
        actions.put("delete", Map.of(
                "href", linkTo(methodOn(SensorController.class).deleteSensor(id)).toString(),
                "method", "DELETE"
        ));

        Map<String, Object> response = new HashMap<>();
        response.put("id", sensor.getId());
        response.put("name", sensor.getName());
        response.put("temperature", sensor.getData());
        response.put("status", sensor.getStatus());
        response.put("_links", links);
        response.put("_actions", actions);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Sensor>> updateSensor(@PathVariable String id, @RequestBody Sensor updatedSensor) {
        Optional<Sensor> existingSensorOpt = sensorService.getSensorById(id);
        if (existingSensorOpt.isEmpty()) {
            throw new RuntimeException("Sensor not found");
        }

        Sensor existingSensor = existingSensorOpt.get();

        existingSensor.setName(updatedSensor.getName());
        existingSensor.setData(updatedSensor.getData());
        existingSensor.setStatus(updatedSensor.getStatus());

        sensorService.updateSensorStatus(existingSensor);

        Sensor savedSensor = sensorService.createSensor(existingSensor);
        EntityModel<Sensor> sensorModel = EntityModel.of(savedSensor);

        Link selfLink = linkTo(methodOn(SensorController.class).getSensorById(savedSensor.getId())).withSelfRel();
        Link allSensorsLink = linkTo(methodOn(SensorController.class).getAllSensors()).withRel("all");
        Link deleteLink = linkTo(methodOn(SensorController.class).deleteSensor(savedSensor.getId())).withRel("delete");
        sensorModel.add(selfLink, allSensorsLink, deleteLink);

        return ResponseEntity.ok(sensorModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Sensor>> createSensor(@RequestBody Sensor sensor) {
        Sensor createdSensor = sensorService.createSensor(sensor);

        if (createdSensor.getStatus()) {
            sensorService.startSensorSpamming(createdSensor);
        }

        EntityModel<Sensor> sensorModel = EntityModel.of(createdSensor);
        Link selfLink = linkTo(methodOn(SensorController.class).getSensorById(createdSensor.getId())).withSelfRel();
        Link allSensorsLink = linkTo(methodOn(SensorController.class).getAllSensors()).withRel("all");
        sensorModel.add(selfLink, allSensorsLink);

        return ResponseEntity
                .created(selfLink.toUri())
                .body(sensorModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<String>> deleteSensor(@PathVariable String id) {
        Optional<Sensor> sensorOpt = sensorService.getSensorById(id);
        if (sensorOpt.isEmpty()) {
            throw new RuntimeException("Sensor not found");
        }

        Sensor sensorToDelete = sensorOpt.get();

        sensorService.stopSensorSpamming(sensorToDelete);

        sensorService.deleteSensor(id);

        EntityModel<String> responseModel = EntityModel.of("Sensor deleted");
        Link allSensorsLink = linkTo(methodOn(SensorController.class).getAllSensors()).withRel("all");
        responseModel.add(allSensorsLink);

        return ResponseEntity.ok(responseModel);
    }
}
