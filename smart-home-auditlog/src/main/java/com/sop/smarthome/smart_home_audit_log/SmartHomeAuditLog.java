package com.sop.smarthome.smart_home_audit_log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sop.smarthome")
public class SmartHomeAuditLog {

    public static void main(String[] args) {
        SpringApplication.run(SmartHomeAuditLog.class, args);
    }
}
