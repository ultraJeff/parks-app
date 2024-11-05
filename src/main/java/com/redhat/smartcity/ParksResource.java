package com.redhat.smartcity;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;

import io.smallrye.mutiny.Uni;

@Path("/parks")
public class ParksResource {

    @Inject
    ParkGuard guard;

    @Operation(
        summary = "Get Parks",
        description = "A description"
    )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Park> getParks() {
        return Park.listAll();
    }

    @Operation(
        summary = "summary",
        description = "description"
    )
    @RolesAllowed({"Admin"})
    @PUT
    @Transactional
    public void updatePark(Park insertedPark) {
        Park
            .<Park>findByIdOptional(insertedPark.id)
            .ifPresentOrElse(
                park -> {
                    park.name = insertedPark.name;
                    park.city = insertedPark.city;
                    park.size = insertedPark.size;
                    park.status = insertedPark.status;
                    park.persist();
                },
                NotFoundException::new
            );
    }

    @POST
    @Path("{id}/weathercheck")
    @Transactional
    public Uni<Void> sendWeatherEvents(@PathParam("id") Long id) {
        return Park
            .<Park>findByIdOptional(id)
            .map(guard::checkWeatherForPark)
            .orElseThrow(NotFoundException::new);
    }
}