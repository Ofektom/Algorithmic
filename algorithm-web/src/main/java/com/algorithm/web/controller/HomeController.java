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

        // Prepare structured problem list for voice/navigation
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
    public String problem(@PathVariable String name, Model model) {
        var problem = problemRegistry.findProblem(name);
        if (problem == null) {
            return "redirect:/";
        }

        model.addAttribute("problem", problem);

        // Select default method (first one) and load its source code for display
        if (problem.getMethods() != null && !problem.getMethods().isEmpty()) {
            String defaultMethod = problem.getMethods().get(0); // first method as default
            String methodCode = sourceCodeService.getMethodSourceCode(problem.getClassName(), defaultMethod);

            model.addAttribute("selectedMethod", defaultMethod);
            model.addAttribute("selectedMethodCode", methodCode != null ? methodCode : ""); // empty string if no code
        }

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
            // Parse JSON inputs
            ObjectMapper mapper = new ObjectMapper();
            List<?> inputsList = mapper.readValue(inputsJson, List.class);
            Object[] parsedInputs = inputsList.toArray();

            // Execute algorithm
            Map<String, Object> result = executionService.execute(className, methodName, parsedInputs);

            // Get source code of the executed method
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
        // Flash attributes are automatically added to the model by Spring
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