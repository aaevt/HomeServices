package org.example;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder
                .forPort(8088)
                .addService((BindableService) new SensorService())
                .build();
        server.start();
        server.awaitTermination();
    }
}
