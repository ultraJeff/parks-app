package com.redhat.smartcity.weather;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;


@RegisterRestClient(configKey = "weather-api")
@Path("/warnings")
public interface WeatherService {

    @GET
    @ClientHeaderParam(name = "test", value = "{name}")
    @Path("/{city}")
    Uni<List<WeatherWarning>> getWarningsByCity(@PathParam("city") String city);
        
}
