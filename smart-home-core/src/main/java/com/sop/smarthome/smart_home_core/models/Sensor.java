package com.sop.smarthome.smart_home_core.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sensors")
public class Sensor {

    @Id
    private String id;
    private String name;
    private double data;
    private boolean status;

    public Sensor() {}

    public Sensor(String name, double data, boolean status) { this.name = name; this.data = data; this.status = status; }

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getData() { return data; }

    public void setData(double data) {
        this.data = data;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
