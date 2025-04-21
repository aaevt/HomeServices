package org.example;

import io.grpc.stub.StreamObserver;
import smarthome.SensorServiceGrpc;
import smarthome.SmartHomeCore.*;
import smarthome.SmartHomeCore;


import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

public class SensorService extends SensorServiceGrpc.SensorServiceImplBase {
    private static final Map<String, SmartHomeCore.Sensor> sensorDatabase = new HashMap<>();

    public SensorService() {
        sensorDatabase.put("1", SmartHomeCore.Sensor.newBuilder().setId("1").setName("Temperature Sensor").setData(22.5).setStatus(true).build());
        sensorDatabase.put("2", SmartHomeCore.Sensor.newBuilder().setId("2").setName("Humidity Sensor").setData(50.0).setStatus(true).build());
    }

    @Override
    public void getAllSensors(SmartHomeCore.Empty request, StreamObserver<SmartHomeCore.SensorList> responseObserver) {
        SmartHomeCore.SensorList.Builder sensorListBuilder = SmartHomeCore.SensorList.newBuilder();

        sensorDatabase.values().forEach(sensorListBuilder::addSensors);

        responseObserver.onNext(sensorListBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getSensorById(SmartHomeCore.SensorId request, StreamObserver<SmartHomeCore.Sensor> responseObserver) {
        String id = request.getId();
        SmartHomeCore.Sensor sensor = sensorDatabase.get(id);

        if (sensor != null) {
            responseObserver.onNext(sensor);
        } else {
            responseObserver.onError(new Throwable("Sensor with ID " + id + " not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void createSensor(SmartHomeCore.Sensor request, StreamObserver<SmartHomeCore.Sensor> responseObserver) {
        String id = request.getId();

        if (sensorDatabase.containsKey(id)) {
            responseObserver.onError(new Throwable("Sensor with ID " + id + " already exists"));
            return;
        }

        sensorDatabase.put(id, request);
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void updateSensor(SmartHomeCore.Sensor request, StreamObserver<SmartHomeCore.Sensor> responseObserver) {
        String id = request.getId();
        SmartHomeCore.Sensor existingSensor = sensorDatabase.get(id);

        if (existingSensor == null) {
            responseObserver.onError(new Throwable("Sensor with ID " + id + " not found"));
            return;
        }

        sensorDatabase.put(id, request);
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteSensor(SensorId request, StreamObserver<ResponseMessage> responseObserver) {
        String id = request.getId();
        SmartHomeCore.Sensor deletedSensor = sensorDatabase.remove(id);

        if (deletedSensor == null) {
            responseObserver.onError(new Throwable("Sensor with ID " + id + " not found"));
            return;
        }

        SmartHomeCore.ResponseMessage responseMessage = SmartHomeCore.ResponseMessage.newBuilder()
                .setMessage("Sensor with ID " + id + " deleted successfully")
                .build();
        responseObserver.onNext(responseMessage);
        responseObserver.onCompleted();
    }
}
