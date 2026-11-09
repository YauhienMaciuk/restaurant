package com.ingroup.restaurant.controller;

import com.ingroup.restaurant.api.DishSuitabilityRequest;
import com.ingroup.restaurant.api.DishSuitabilityResponse;
import com.ingroup.restaurant.domain.Dish;
import com.ingroup.restaurant.service.DishSuitabilityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class DishSuitabilityController {

    private static final Logger log = LoggerFactory.getLogger(DishSuitabilityController.class);

    private final DishSuitabilityService dishSuitabilityService;

    public DishSuitabilityController(DishSuitabilityService dishSuitabilityService) {
        this.dishSuitabilityService = dishSuitabilityService;
    }

    @PostMapping("/dish-suitability")
    public DishSuitabilityResponse score(@Valid @RequestBody DishSuitabilityRequest request) {
        String time = request.time();
        Dish dish = Dish.fromName(request.dish()).orElseThrow(
                () -> new IllegalStateException("Dish passed validation but cannot be parsed: " + request.dish())
        );
        int score = dishSuitabilityService.score(LocalTime.parse(time), dish);

        log.info("Dish suitability: dish={}, time={}, score={}", dish, time, score);

        return new DishSuitabilityResponse(score);
    }
}
