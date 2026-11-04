package com.algorithm.web.controller;

import com.algorithm.web.service.MissedQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InsightsController.class, properties = "app.insights.key=secret-key")
@Import(MissedQuestionService.class)
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MissedQuestionService missedQuestions;

    @Test
    void showsMissedQuestionsWithTheRightKey() throws Exception {
        missedQuestions.record("design a parking lot");
        mockMvc.perform(get("/insights").param("key", "secret-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("insights"))
                .andExpect(content().string(containsString("design a parking lot")));
    }

    @Test
    void hiddenWithoutOrWithWrongKey() throws Exception {
        mockMvc.perform(get("/insights")).andExpect(status().isNotFound());
        mockMvc.perform(get("/insights").param("key", "nope")).andExpect(status().isNotFound());
    }
}

@WebMvcTest(controllers = InsightsController.class)
@Import(MissedQuestionService.class)
class InsightsControllerOffTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void offWhenNoKeyIsConfigured() throws Exception {
        mockMvc.perform(get("/insights").param("key", "")).andExpect(status().isNotFound());
    }
}
