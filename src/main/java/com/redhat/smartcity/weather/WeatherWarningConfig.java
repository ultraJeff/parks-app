package com.redhat.smartcity.weather;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "weather")
public interface WeatherWarningConfig {
    @WithDefault("rain")
    String weatherType();
}
