package com.ingroup.restaurant.controller;

import com.ingroup.restaurant.domain.Dish;
import com.ingroup.restaurant.service.DishSuitabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DishSuitabilityController.class)
class DishSuitabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishSuitabilityService service;

    private ResultActions postJson(String body) throws Exception {
        return mockMvc.perform(post("/dish-suitability").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void returnsScoreFromService() throws Exception {
        when(service.score(any(), any())).thenReturn(7);

        postJson("""
                {"time": "09:30", "dish": "Pancakes"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(7));

        verify(service).score(LocalTime.of(9, 30), Dish.Pancakes);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pancakes", "PANCAKES", "icecream", "cHiCkEn"})
    void dishIsCaseInsensitive(String dish) throws Exception {
        when(service.score(any(), any())).thenReturn(1);

        postJson("""
                {"time": "09:30", "dish": "%s"}
                """.formatted(dish))
                .andExpect(status().isOk());
    }

    @Test
    void unknownDishReturnsDishNotAllowed() throws Exception {
        postJson("""
                {"time": "09:30", "dish": "Pizza"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("dish"))
                .andExpect(jsonPath("$.errors[0].code").value("DISH_NOT_ALLOWED"))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("dish 'Pizza' is not allowed. Allowed dishes: Pancakes, Chicken, IceCream"));
    }

    @Test
    void blankDishReturnsDishRequired() throws Exception {
        postJson("""
                {"time": "09:30", "dish": ""}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("dish"))
                .andExpect(jsonPath("$.errors[0].code").value("DISH_REQUIRED"))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("dish is required. Allowed dishes: Pancakes, Chicken, IceCream"));
    }

    @Test
    void missingDishReturnsDishRequired() throws Exception {
        postJson("""
                {"time": "09:30"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("dish"))
                .andExpect(jsonPath("$.errors[0].code").value("DISH_REQUIRED"))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("dish is required. Allowed dishes: Pancakes, Chicken, IceCream"));
    }

    @Test
    void invalidTimeReturnsInvalidTimeFormat() throws Exception {
        postJson("""
                {"time": "25:99", "dish": "Pancakes"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("time"))
                .andExpect(jsonPath("$.errors[0].code").value("INVALID_TIME_FORMAT"))
                .andExpect(jsonPath("$.errors[0].message").value("time must be a valid time in HH:mm format"));
    }

    @Test
    void missingTimeReturnsTimeRequired() throws Exception {
        postJson("""
                {"dish": "Pancakes"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("time"))
                .andExpect(jsonPath("$.errors[0].code").value("TIME_REQUIRED"));
    }

    @Test
    void everyInvalidFieldIsReportedSortedByField() throws Exception {
        postJson("""
                {"time": "25:99"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[0].field").value("dish"))
                .andExpect(jsonPath("$.errors[0].code").value("DISH_REQUIRED"))
                .andExpect(jsonPath("$.errors[1].field").value("time"))
                .andExpect(jsonPath("$.errors[1].code").value("INVALID_TIME_FORMAT"));
    }

    @Test
    void malformedJsonReturnsMalformedRequest() throws Exception {
        postJson("not json")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
                .andExpect(jsonPath("$.message").value("Request body is missing or is not valid JSON"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void invalidTimeAndDishAreBothReported() throws Exception {
        postJson("""
                {"time": "99:39", "dish": " PancakeS1 "}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[0].field").value("dish"))
                .andExpect(jsonPath("$.errors[0].code").value("DISH_NOT_ALLOWED"))
                .andExpect(jsonPath("$.errors[1].field").value("time"))
                .andExpect(jsonPath("$.errors[1].code").value("INVALID_TIME_FORMAT"));

        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {"time": "09:30", "dish": "Pizza"}""",
            """
                    {"time": "09:30", "dish": ""}""",
            """
                    {"time": "09:30", "dish": null}""",
            """
                    {"time": "9:30", "dish": "Pancakes"}""",
            """
                    {"time": "24:00", "dish": "Pancakes"}""",
            """
                    {"time": "12:60", "dish": "Chicken"}""",
            """
                    {"dish": "IceCream"}""",
            """
                    {"time": "09:30"}""",
            """
                    {}""",
            "not json"
    })
    void invalidRequestReturns400(String body) throws Exception {
        postJson(body).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
