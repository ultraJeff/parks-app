package com.redhat.smartcity;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyArray;

@QuarkusTest
public class ParksResourceTest {

    @Test
    public void testParksEndpoint() {
        given()
          .when().get("/parks")
          .then()
             .statusCode(200)
             .body(not(emptyArray()));
    }

}