package org.example;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import smarthome.SensorServiceGrpc;
import smarthome.SmartHomeCore.*;

import java.util.concurrent.TimeUnit;

public class QueueClient implements AutoCloseable {
    private final ManagedChannel channel;
    private final SensorServiceGrpc.SensorServiceBlockingStub blockingStub;

    public QueueClient(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = SensorServiceGrpc.newBlockingStub(channel);
    }

    public Sensor getAnalyze(String message) {
        try {
            SensorId.Builder sensorIdBuilder = SensorId.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(message, sensorIdBuilder);
            SensorId sensorId = sensorIdBuilder.build();

            System.out.println("\n3. SensorId received from builder:\n" + sensorId);

            return blockingStub.getSensorById(sensorId);
        } catch (InvalidProtocolBufferException e) {
            System.err.println("Error parsing message into SensorId: " + e.getMessage());
            e.printStackTrace();
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        try {
            if (channel != null) {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while shutting down channel");
        }
    }
}
