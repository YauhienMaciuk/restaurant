package com.ingroup.restaurant.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.config.import[0]=classpath:test-dishes/pancakes.yml",
                "spring.config.import[1]=classpath:test-dishes/chicken.yml",
                "spring.config.import[2]=classpath:test-dishes/icecream.yml"
        })
class DishSuitabilityControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> postJson(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity("/dish-suitability", new HttpEntity<>(body, headers), String.class);
    }

    @ParameterizedTest
    @CsvSource({"09:30,Pancakes,7", "12:30,Chicken,5", "15:00,IceCream,3", "03:00,Pancakes,0", "23:00,IceCream,0"})
    void validRequestReturnsScore(String time, String dish, int score) {
        ResponseEntity<String> response = postJson("""
                {"time": "%s", "dish": "%s"}
                """.formatted(time, dish));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace("""
                {"score": %d}
                """.formatted(score));
    }

    @Test
    void dishIsCaseInsensitive() {
        ResponseEntity<String> response = postJson("""
                {"time": "09:30", "dish": "icecream"}
                """);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void unknownDishReturns400() {
        ResponseEntity<String> response = postJson("""
                {"time": "09:30", "dish": "Pizza"}
                """);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace("""
                {
                  "code": "VALIDATION_FAILED",
                  "message": "Request validation failed",
                  "errors": [
                    {
                      "field": "dish",
                      "code": "DISH_NOT_ALLOWED",
                      "message": "dish 'Pizza' is not allowed. Allowed dishes: Pancakes, Chicken, IceCream"
                    }
                  ]
                }
                """);
    }

    @Test
    void invalidTimeReturns400() {
        ResponseEntity<String> response = postJson("""
                {"time": "25:99", "dish": "Pancakes"}
                """);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void missingFieldsReturn400() {
        assertThat(postJson("{}").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getIsNotAllowed() {
        ResponseEntity<String> response = restTemplate.getForEntity("/dish-suitability", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
