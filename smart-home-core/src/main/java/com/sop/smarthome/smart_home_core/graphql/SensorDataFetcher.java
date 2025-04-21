package com.sop.smarthome.smart_home_core.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsMutation;
import com.sop.smarthome.smart_home_core.models.Sensor;
import com.sop.smarthome.smart_home_core.service.SensorService;

import java.util.List;
import java.util.Optional;

@DgsComponent
public class SensorDataFetcher {

    private final SensorService sensorService;

    public SensorDataFetcher(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @DgsQuery
    public List<Sensor> sensors() {
        return sensorService.getAllSensors();
    }

    @DgsQuery
    public Optional<Sensor> sensor(String id) {
        return sensorService.getSensorById(id);
    }

    @DgsMutation
    public Sensor createSensor(String name, double data, boolean status) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Sensor name cannot be null or empty");
        }

        Sensor sensor = new Sensor(name, data, status);
        return sensorService.createSensor(sensor);
    }

    @DgsMutation
    public String deleteSensor(String id) {
        sensorService.deleteSensor(id);
        return "Sensor deleted";
    }
}
