package com.redhat.smartcity;


import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.smallrye.jwt.build.Jwt;


class UserCredentials {
	public String username;
	public String password;
}

class UserResponse {
    public String username;

    UserResponse(String username) {
        this.username = username;
    }
}


@Path( "/auth" )
@RequestScoped
public class AuthResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    UserService userService;

    @Inject
    JwtGenerator jwtService;

    MeterRegistry registry;

    AuthResource(MeterRegistry registry) {
        this.registry = registry;
    }


    @POST
    @Path("/login")
    public Response login(UserCredentials credentials) {
        Log.info("Attempting to login");
        if (userService.authenticate(credentials.username, credentials.password)) {
            var token = jwtService.generateForUser(credentials.username);
            return Response.ok(token).build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }


    public String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2V4YW1wbGUuY29tL2lzc3VlciIsInVwbiI6ImFkbWluIiwiZ3JvdXBzIjpbIkFkbWluIiwiVXNlciJdLCJpYXQiOjE3MDE3ODU4NDIsImV4cCI6MTcwMTc4NjE0MiwianRpIjoiMDk2NGYyNDAtMDVlMi00MjFhLWI5YTktYTc5NWIwN2ZjZjViIn0.Zg0k-j3j-xrRX3Yp0UDHif66AqqKzgU-bl_n0j5VBriE2dbK1sAA0G9VkfWC5DcVGZMwwRe4wCBf0UX7M9XxpTdAWzUt1Uqk3acBQtbUah15WJwJ8c3BhhTxJIsFVrtNXeLsJLCswtx52gv8iwsxsichueBooSpi9QOulb3doeXm9yAo_TcGW8T5hMxg9Rc7AzUY2uefJ_icXREKAz79c7Es0FWsv3uHUGAuB5ksVHmhPWGpInxETVflqZsq5IqG2zZtuW97xyVlPRKZJx2kFDhLIznGEeN0SYB8_LuqpOb93orovopYesH8Haf_rsIQg8KXWB1U3_8ed3cU6V99og";

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    @RequestScoped
    @Path( "/user" )
    @ClientHeaderParam(name = "Authorization", value="Bearer {token}")
    @RolesAllowed( { "Admin", "User" } )
    public UserResponse user(@Context SecurityContext context) {
        registry.counter("auth.user", "type", "getUser").increment();
        Log.warn(jsonWebToken.getName());
        if (!context.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        }

        String username = context.getUserPrincipal().getName();
        return new UserResponse(username);
    }
}
