package com.algorithm.web.controller;

import com.algorithm.web.service.AlgorithmExecutionService;
import com.algorithm.web.service.AlgorithmSourceCodeService;
import com.algorithm.web.service.ProblemRegistryService;
import com.algorithm.web.model.ProblemInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        addCategories(model);
        return "index";
    }

    @GetMapping("/browse")
    public String browse(Model model) {
        addCategories(model);
        return "browse";
    }

    // History and settings live in the browser (localStorage); the server only serves the pages
    @GetMapping("/history")
    public String history() {
        return "history";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    private void addCategories(Model model) {
        List<String> categories = problemRegistry.getCategories();
        Map<String, Integer> counts = new LinkedHashMap<>();
        // Problem names per category let the page show progress from the browser's "ran" list
        Map<String, List<String>> names = new LinkedHashMap<>();
        for (String c : categories) {
            List<ProblemInfo> problems = problemRegistry.getProblemsForCategory(c);
            counts.put(c, problems.size());
            names.put(c, problems.stream().map(ProblemInfo::getName).toList());
        }
        model.addAttribute("categories", categories);
        model.addAttribute("categoryCounts", counts);
        model.addAttribute("categoryProblems", names);
    }

    // {*category} captures the rest of the path so names containing "/" (e.g. "Math/Geometry") resolve
    @GetMapping("/category/{*category}")
    public String category(@PathVariable String category, Model model) {
        if (category.startsWith("/")) {
            category = category.substring(1); // drop the leading "/" included by the capture
        }
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

        Map<String, String> labels = new LinkedHashMap<>();
        if (problem.getMethods() != null) {
            problem.getMethods().forEach(m -> labels.put(m, methodLabel(m)));
        }
        model.addAttribute("methodLabels", labels);
        model.addAttribute("related", problemRegistry.getProblemsForCategory(problem.getCategory()).stream()
                .filter(p -> !p.getName().equals(problem.getName()))
                .limit(4)
                .toList());

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

    // Kept for existing links: lands where the search box would, without a results list in between
    @GetMapping("/search")
    public String search(@RequestParam String q) {
        Map<String, Object> target = resolve(Map.of("query", q));
        if (target.containsKey("url")) {
            return "redirect:" + target.get("url");
        }
        return "redirect:/solution?q=" + UriUtils.encodeQueryParam(q, StandardCharsets.UTF_8);
    }

    /**
     * Resolve a spoken or typed question to where its solution lives: a built-in problem, a category,
     * or (type "ai") nowhere built-in, in which case the client opens the generated-solution page.
     */
    @PostMapping("/api/resolve")
    @ResponseBody
    public Map<String, Object> resolve(@RequestBody Map<String, String> body) {
        var resolution = problemRegistry.resolve(body.getOrDefault("query", ""));
        Map<String, Object> response = new LinkedHashMap<>();
        if (resolution.match() != null) {
            response.put("type", "problem");
            response.put("name", resolution.match().getName());
            response.put("url", problemUrl(resolution.match()));
        } else if (resolution.category() != null) {
            response.put("type", "category");
            response.put("name", resolution.category());
            response.put("url", "/category/" + UriUtils.encodePath(resolution.category(), StandardCharsets.UTF_8));
        } else {
            response.put("type", "ai");
            if (resolution.suggestion() != null) {
                response.put("suggestion", Map.of(
                        "name", resolution.suggestion().getName(),
                        "url", problemUrl(resolution.suggestion())));
            }
        }
        return response;
    }

    @GetMapping("/solution")
    public String solution() {
        // The page reads the question from ?q= or sessionStorage and fetches the answer itself
        return "solution";
    }

    /** "computeWithHashMapTwoPass" -> "Hash map two pass"; acronyms such as BFS, DP and XOR stay upper case. */
    public static String methodLabel(String method) {
        if (method == null || method.isEmpty()) {
            return "";
        }
        String core = method.replaceFirst("^compute(With|Using|By)?", "");
        if (core.isEmpty()) {
            core = method;
        }
        String[] words = core.split("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
        StringBuilder label = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            boolean acronym = word.length() > 1 && word.equals(word.toUpperCase());
            String part = acronym ? word : word.toLowerCase();
            if (label.length() == 0) {
                part = Character.toUpperCase(part.charAt(0)) + part.substring(1);
            } else {
                label.append(' ');
            }
            label.append(part);
        }
        return label.toString();
    }

    private static String problemUrl(ProblemInfo problem) {
        return "/problem/" + UriUtils.encodePathSegment(problem.getName(), StandardCharsets.UTF_8);
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<com.algorithm.web.model.ProblemInfo> apiSearch(@RequestParam String q) {
        return problemRegistry.searchProblems(q);
    }
}