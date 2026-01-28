package com.algorithm.web.controller;

import com.algorithm.web.service.AlgorithmExecutionService;
import com.algorithm.web.service.AlgorithmSourceCodeService;
import com.algorithm.web.service.ProblemRegistryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProblemRegistryService problemRegistry;

    @Autowired
    private AlgorithmExecutionService executionService;

    @Autowired
    private AlgorithmSourceCodeService sourceCodeService;

    @GetMapping("/")
    public String home(Model model) {
        List<String> categories = problemRegistry.getCategories();
        model.addAttribute("categories", categories);

        List<Map<String, Object>> problemsForVoice = problemRegistry.getAllProblems().stream()
                .map(problem -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("title", problem.getName() != null ? problem.getName() : "");
                    entry.put("slug", problem.getClassName() != null && !problem.getClassName().isEmpty()
                            ? problem.getClassName()
                            : (problem.getName() != null ? problem.getName() : ""));
                    entry.put("name", problem.getName());
                    entry.put("category", problem.getCategory());
                    entry.put("shortDescription", problem.getShortDescription());
                    entry.put("aliases", problem.getAliases() != null ? problem.getAliases() : List.of());
                    entry.put("keywords", problem.getKeywords() != null ? problem.getKeywords() : List.of());
                    return entry;
                })
                .collect(Collectors.toList());

        model.addAttribute("problemsJson", problemsForVoice);
        model.addAttribute("categoriesJson", categories);

        return "index";
    }

    @GetMapping("/category/{category}")
    public String category(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        model.addAttribute("problems", problemRegistry.getProblemsForCategory(category));
        return "category";
    }

    @GetMapping("/problem/{name}")
    public String problem(@PathVariable String name,
            @RequestParam(value = "method", required = false) String selectedMethodParam,
            Model model) {
        var problem = problemRegistry.findProblem(name);
        if (problem == null) {
            return "redirect:/";
        }

        model.addAttribute("problem", problem);

        // Determine selected method: from query param, or default to first method
        String selectedMethod = selectedMethodParam;
        if (selectedMethod == null || !problem.getMethods().contains(selectedMethod)) {
            selectedMethod = problem.getMethods() != null && !problem.getMethods().isEmpty()
                    ? problem.getMethods().get(0)
                    : null;
        }

        String methodCode = null;
        if (selectedMethod != null) {
            methodCode = sourceCodeService.getMethodSourceCode(problem.getClassName(), selectedMethod);
        }

        model.addAttribute("selectedMethod", selectedMethod);
        model.addAttribute("selectedMethodCode", methodCode != null ? methodCode : "");

        return "problem";
    }

    @PostMapping("/execute")
    public String execute(@RequestParam String className,
            @RequestParam String methodName,
            @RequestParam String problemName,
            @RequestParam String category,
            @RequestParam String inputsJson,
            @RequestParam String[] paramNames,
            @RequestParam String[] paramTypes,
            RedirectAttributes redirectAttributes) {
        try {
            // Parse JSON inputs - allow empty array
            ObjectMapper mapper = new ObjectMapper();
            List<?> inputsList;
            if (inputsJson == null || inputsJson.trim().isEmpty()) {
                inputsList = new ArrayList<>(); // empty inputs
            } else {
                inputsList = mapper.readValue(inputsJson, List.class);
            }
            Object[] parsedInputs = inputsList.toArray();

            // Execute algorithm
            Map<String, Object> result = executionService.execute(className, methodName, parsedInputs);

            // Get source code of executed method
            String methodSourceCode = sourceCodeService.getMethodSourceCode(className, methodName);

            // Get problem info
            var problem = problemRegistry.findProblem(problemName);

            // Flash attributes for result page
            redirectAttributes.addFlashAttribute("success", result.get("success"));
            redirectAttributes.addFlashAttribute("result", result.get("result"));
            redirectAttributes.addFlashAttribute("error", result.get("error"));
            redirectAttributes.addFlashAttribute("methodName", methodName);
            redirectAttributes.addFlashAttribute("methodSourceCode", methodSourceCode);
            redirectAttributes.addFlashAttribute("problemName", problemName);
            redirectAttributes.addFlashAttribute("category", category);
            redirectAttributes.addFlashAttribute("className", className);
            redirectAttributes.addFlashAttribute("allMethods", problem != null ? problem.getMethods() : List.of());
            redirectAttributes.addFlashAttribute("inputsJson", inputsJson);
            redirectAttributes.addFlashAttribute("paramNames", Arrays.asList(paramNames));
            redirectAttributes.addFlashAttribute("paramTypes", Arrays.asList(paramTypes));

            return "redirect:/result";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("success", false);
            redirectAttributes.addFlashAttribute("error", "Execution failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("problemName", problemName);
            redirectAttributes.addFlashAttribute("category", category);
            return "redirect:/result";
        }
    }

    @GetMapping("/result")
    public String result(Model model) {
        System.out.println("Result page accessed - Model keys: " + model.asMap().keySet());
        return "result";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        model.addAttribute("query", q);
        model.addAttribute("results", problemRegistry.searchProblems(q));
        return "search";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<com.algorithm.web.model.ProblemInfo> apiSearch(@RequestParam String q) {
        return problemRegistry.searchProblems(q);
    }
}