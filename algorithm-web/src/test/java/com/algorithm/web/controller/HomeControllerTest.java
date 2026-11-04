package com.algorithm.web.controller;

import com.algorithm.web.service.AlgorithmExecutionService;
import com.algorithm.web.service.AlgorithmSourceCodeService;
import com.algorithm.web.service.ProblemRegistryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@Import(ProblemRegistryService.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlgorithmExecutionService executionService;

    @MockBean
    private AlgorithmSourceCodeService sourceCodeService;

    @Test
    void categoryWithoutSlashResolves() throws Exception {
        mockMvc.perform(get("/category/Dynamic Programming"))
                .andExpect(status().isOk())
                .andExpect(view().name("category"))
                .andExpect(model().attribute("category", "Dynamic Programming"))
                .andExpect(model().attribute("problems", not(empty())));
    }

    @Test
    void categoriesContainingSlashResolve() throws Exception {
        for (String category : List.of("Heap/Priority Queue", "Math/Geometry")) {
            mockMvc.perform(get("/category/" + category))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("category", category))
                    .andExpect(model().attribute("problems", not(empty())));
        }
    }

    @Test
    void percentEncodedSegmentsAreDecoded() throws Exception {
        // Same URL voice navigation builds: each segment encoded, "/" kept as-is
        // URI (not a String template) so MockMvc sends it as-is instead of re-encoding "%20"
        mockMvc.perform(get(URI.create("/category/Heap/Priority%20Queue")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("category", "Heap/Priority Queue"))
                .andExpect(model().attribute("problems", not(empty())));
    }

    @Test
    void categoryWithoutNameRendersEmptyList() throws Exception {
        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("category", ""))
                .andExpect(model().attribute("problems", empty()));
    }

    @Test
    void unknownCategoryRendersEmptyList() throws Exception {
        mockMvc.perform(get("/category/Nope"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("problems", empty()));
    }

    @Test
    void resolveReturnsProblemUrl() throws Exception {
        mockMvc.perform(post("/api/resolve").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": \"solve two sum\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("problem"))
                .andExpect(jsonPath("$.name").value("Two Sum"))
                .andExpect(jsonPath("$.url").value("/problem/Two%20Sum"));
    }

    @Test
    void resolveReturnsCategoryUrlKeepingSlash() throws Exception {
        mockMvc.perform(post("/api/resolve").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": \"go to math geometry\"}"))
                .andExpect(jsonPath("$.type").value("category"))
                .andExpect(jsonPath("$.url").value("/category/Math/Geometry"));
    }

    @Test
    void resolveFallsBackToAiWithSuggestion() throws Exception {
        mockMvc.perform(post("/api/resolve").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": \"given a string s return the number of palindromic substrings in it\"}"))
                .andExpect(jsonPath("$.type").value("ai"))
                .andExpect(jsonPath("$.suggestion.name").value("Longest Palindromic Substring"))
                .andExpect(jsonPath("$.suggestion.url").value("/problem/Longest%20Palindromic%20Substring"));
    }

    @Test
    void resolveUnknownQuestionHasNoSuggestion() throws Exception {
        mockMvc.perform(post("/api/resolve").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": \"design a least recently used cache with get and put in constant time\"}"))
                .andExpect(jsonPath("$.type").value("ai"))
                .andExpect(jsonPath("$.suggestion").doesNotExist());
    }

    @Test
    void solutionPageRenders() throws Exception {
        mockMvc.perform(get("/solution"))
                .andExpect(status().isOk())
                .andExpect(view().name("solution"));
    }

    @Test
    void searchLinksGoStraightToTheSolution() throws Exception {
        mockMvc.perform(get("/search").param("q", "two sum"))
                .andExpect(redirectedUrl("/problem/Two%20Sum"));
        mockMvc.perform(get("/search").param("q", "go to math geometry"))
                .andExpect(redirectedUrl("/category/Math/Geometry"));
        mockMvc.perform(get("/search").param("q", "design an lru cache & more"))
                .andExpect(redirectedUrl("/solution?q=design%20an%20lru%20cache%20%26%20more"));
    }

    @Test
    void browseHistoryAndSettingsPagesRender() throws Exception {
        mockMvc.perform(get("/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("browse"))
                .andExpect(model().attributeExists("categories", "categoryCounts", "categoryProblems"));
        mockMvc.perform(get("/history")).andExpect(status().isOk()).andExpect(view().name("history"));
        mockMvc.perform(get("/settings")).andExpect(status().isOk()).andExpect(view().name("settings"));
    }

    @Test
    void homeRendersWithCategoryProblems() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categoryProblems"))
                .andExpect(content().string(containsString("What do you want to solve?")));
    }

    @Test
    void problemPageHasReadableMethodNamesAndSourceIndicator() throws Exception {
        mockMvc.perform(get("/problem/Two Sum"))
                .andExpect(status().isOk())
                .andExpect(view().name("problem"))
                .andExpect(model().attributeExists("methodLabels", "related"))
                .andExpect(content().string(containsString("Built-in solution")))
                .andExpect(content().string(containsString("Hash map")));
    }

    @Test
    void methodLabelsAreReadable() {
        org.junit.jupiter.api.Assertions.assertEquals("Hash map", HomeController.methodLabel("computeWithHashMap"));
        org.junit.jupiter.api.Assertions.assertEquals("Hash map two pass", HomeController.methodLabel("computeWithHashMapTwoPass"));
        org.junit.jupiter.api.Assertions.assertEquals("BFS", HomeController.methodLabel("computeWithBFS"));
        org.junit.jupiter.api.Assertions.assertEquals("Recursive", HomeController.methodLabel("computeRecursive"));
        org.junit.jupiter.api.Assertions.assertEquals("DFS and memo", HomeController.methodLabel("computeWithDFSAndMemo"));
    }

    @Test
    void stylesheetAndScriptsAreLinkedWithAContentHash() throws Exception {
        // A changed hash on every deploy is what stops browsers using a stale cached stylesheet
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("(?s).*href=\"/css/style-[0-9a-f]{32}\\.css\".*")))
                .andExpect(content().string(matchesPattern("(?s).*src=\"/js/app-[0-9a-f]{32}\\.js\".*")));
    }
}
