package com.ingroup.restaurant.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void specDescribesDishSuitabilityContract() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity("/v3/api-docs", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode spec = response.getBody();
        JsonNode responses = spec.at("/paths/~1dish-suitability/post/responses");
        assertThat(responses.at("/200/content/*~1*/schema/$ref").asText()).endsWith("/DishSuitabilityResponse");
        assertThat(responses.at("/400/content/*~1*/schema/$ref").asText()).endsWith("/ApiError");

        JsonNode request = spec.at("/components/schemas/DishSuitabilityRequest");
        assertThat(request.at("/required").toString()).contains("time", "dish");
        assertThat(request.at("/properties/time/pattern").asText()).isEqualTo("([01]\\d|2[0-3]):[0-5]\\d");
    }

    @Test
    void swaggerUiIsServed() {
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui/index.html", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
