package com.redhat.smartcity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import com.redhat.smartcity.weather.WeatherWarningConfig;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.quarkus.logging.Log;

@Path("/jeff")
@Produces("text/plain")
public class JeffResource {

    private final MeterRegistry registry;
    private final LinkedList<Long> list = new LinkedList<>();

    JeffResource(MeterRegistry registry) {
        this.registry = registry;
        registry.gaugeCollectionSize("jeff.list.size", Tags.empty(), list);
    }

    @Inject
    WeatherWarningConfig weatherWarningConfig;

    @ConfigProperty(name = "example.my.name")
    String exampleMyName;

    @GET
    @Path("gauge/{number}")
    public Long checkListSize(@PathParam("number") long number) {
        if (number == 2 || number % 2 == 0) {
            list.add(number);
        } else {
            try {
                number = list.removeFirst();
            } catch (NoSuchElementException nse) {
                number = 0;
            }
        }

        return number;
    }

    @GET
    @Path("prime/{number}")
    @ClientHeaderParam(name="foo", value="{exampleMyName}")
    public String isPrime(@PathParam("number") long number, @Context HttpHeaders headers) {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("x-foo", exampleMyName);
        if (number < 1) {
            Log.warn("It's raining " + weatherWarningConfig.weatherType());
            registry.counter("jeff.prime.number", "type", "not-natural").increment();
            return "Only natural numbers can be prime numbers";
        }
        if (number == 1) {
            Log.warn(exampleMyName + " loves the number 1");
            // headers.getRequestHeaders().addAll(exampleMyName, requestHeaders);
            registry.counter("jeff.prime.number", "type", "one").increment();
            return number + " is not prime.";
        }
        if (number == 2 || number % 2 == 0) {
            registry.counter("jeff.prime.number", "type", "even").increment();
            return number + " is not prime.";
        }
        if (timedTestPrimeNumber(number)) {
            registry.counter("jeff.prime.number", "type", "prime").increment();
            return number + " is prime.";
        } else {
            registry.counter("jeff.prime.number", "type", "not-prime").increment();
            return number + " is not prime.";
        }
    }

    protected boolean timedTestPrimeNumber(long number) {
        Timer.Sample sample = Timer.start(registry);
        boolean isPrime = testPrimeNumber(number);
        sample.stop(
            registry.timer("jeff.prime.number.test", "prime", Boolean.toString(isPrime))
        );
        return isPrime;
    }

    protected boolean testPrimeNumber(long number) {
        for (int i = 3; i < Math.floor(Math.sqrt(number)) + 1; i = i + 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    
}
