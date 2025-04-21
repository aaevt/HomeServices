package com.sop.smarthome.smart_home_core.repositories;

import com.sop.smarthome.smart_home_core.models.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> { }
