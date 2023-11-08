package com.redhat.smartcity;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;

import io.smallrye.mutiny.Uni;

@Path("/parks")
public class ParksResource {

    @Inject
    ParkGuard guard;

    @Operation(
        summary = "summary",
        description = "description"
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