package com.ingroup.restaurant.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthReturnsUpOverHttp() {
        ResponseEntity<String> response = restTemplate.getForEntity("/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(response.getBody()).isEqualToIgnoringWhitespace("{\"status\":\"UP\"}");
    }

    @Test
    void unknownPathReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/nope", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
