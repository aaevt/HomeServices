package com.sop.smarthome.smart_home_audit_log.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LogProcessor.class);

    public static void processAndLogMessage(String message, String logLevel) {
        try {
        switch (LogLevel.INFO) {
            case "INFO":
                logger.info("Received message: {}", message);
                break;
            case "ERROR":
                logger.error("Error message: {}", message);
                break;
            default:
                logger.warn("Unknown log level! Defaulting to INFO: {}", message);
        }
        } catch (Exception e) {
            logger.error("Error while processing log message: {}", e.getMessage(), e);
        }
    }
}
