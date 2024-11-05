package com.redhat.smartcity;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.redhat.smartcity.weather.WeatherWarning;

public class WeatherWarningsProcessor {

    @Inject
    ParkGuard guard;

    @Incoming("weather-warnings")
    @Transactional
    public void processWeatherWarnings(
        WeatherWarning warning
    ) {
        List<Park> parks = Park.find("city = ?1", warning.city).list();
        for (Park park : parks) {
            guard.updateParkBasedOnWarning(park, warning);
        }

    }
    
}
