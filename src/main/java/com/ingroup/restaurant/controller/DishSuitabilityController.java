package com.ingroup.restaurant.controller;

import com.ingroup.restaurant.api.DishSuitabilityRequest;
import com.ingroup.restaurant.api.DishSuitabilityResponse;
import com.ingroup.restaurant.service.DishSuitabilityService;
import jakarta.validation.Valid;
import java.time.LocalTime;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DishSuitabilityController {

    private final DishSuitabilityService service;

    public DishSuitabilityController(DishSuitabilityService service) {
        this.service = service;
    }

    @PostMapping("/dish-suitability")
    public DishSuitabilityResponse dishSuitability(@Valid @RequestBody DishSuitabilityRequest request) {
        int score = service.score(LocalTime.parse(request.time()), request.dish());
        return new DishSuitabilityResponse(score);
    }
}
