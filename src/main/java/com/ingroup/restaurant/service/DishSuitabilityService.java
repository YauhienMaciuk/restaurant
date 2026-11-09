package com.ingroup.restaurant.service;

import com.ingroup.restaurant.config.DishScoreProperties;
import com.ingroup.restaurant.domain.Dish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Map;

@Service
public class DishSuitabilityService {

    private static final Logger log = LoggerFactory.getLogger(DishSuitabilityService.class);

    private final DishScoreProperties dishScoreProperties;

    public DishSuitabilityService(DishScoreProperties dishScoreProperties) {
        this.dishScoreProperties = dishScoreProperties;
        log.info("Loaded dish scores for {} dishes: {}",
                dishScoreProperties.scores().size(),
                dishScoreProperties.scores().keySet()
        );
    }

    /**
     * Returns the configured score for the dish at the hour of the given time, or 0 if not configured.
     */
    public int score(LocalTime time, Dish dish) {
        int hour = time.getHour();
        Integer score = dishScoreProperties.scores().getOrDefault(dish, Map.of()).get(hour);
        if (score == null) {
            log.debug("No score configured for dish={} hour={}, using 0", dish, hour);
            return 0;
        }

        return score;
    }
}
