package com.algorithm.web.export;

import com.algorithm.web.controller.HomeController;
import com.algorithm.web.model.ProblemInfo;
import com.algorithm.web.service.AlgorithmSourceCodeService;
import com.algorithm.web.service.ProblemRegistryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Writes the problem catalogue and every method's source code as JSON for the static site on Vercel
 * (web/data), so its pages load without waiting for the Java server. Run with
 * {@code ./gradlew :algorithm-web:exportStaticData}; the "Export static data" workflow does it on push.
 *
 * <p>Lives in test sources so the app's jar keeps a single main class.
 */
public final class StaticDataExport {

    public static void main(String[] args) throws IOException {
        Path out = Path.of(args.length > 0 ? args[0] : "web/data");
        ProblemRegistryService registry = new ProblemRegistryService();
        registry.initialize();
        AlgorithmSourceCodeService sources = new AlgorithmSourceCodeService();
        ObjectMapper json = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        Path problemsDir = out.resolve("problems");
        if (Files.isDirectory(problemsDir)) {
            try (Stream<Path> old = Files.list(problemsDir)) {
                for (Path p : old.toList()) {
                    Files.delete(p); // removed problems must not linger
                }
            }
        }
        Files.createDirectories(problemsDir);

        Map<String, String> slugs = new LinkedHashMap<>();
        Set<String> used = new HashSet<>();
        for (ProblemInfo p : registry.getAllProblems()) {
            String base = slug(p.getName());
            String s = base;
            for (int i = 2; !used.add(s); i++) {
                s = base + "-" + i;
            }
            slugs.put(p.getName(), s);
        }

        List<Map<String, Object>> categories = new ArrayList<>();
        int missingCode = 0;
        for (String category : registry.getCategories()) {
            List<ProblemInfo> problems = registry.getProblemsForCategory(category);
            List<Map<String, Object>> summaries = new ArrayList<>();
            for (ProblemInfo p : problems) {
                Map<String, Object> summary = new LinkedHashMap<>();
                summary.put("name", p.getName());
                summary.put("slug", slugs.get(p.getName()));
                summary.put("shortDescription", p.getShortDescription());
                summary.put("methodCount", p.getMethods() == null ? 0 : p.getMethods().size());
                summary.put("aliases", p.getAliases() == null ? List.of() : p.getAliases());
                summary.put("keywords", p.getKeywords() == null ? List.of() : p.getKeywords());
                summaries.add(summary);

                List<Map<String, Object>> methods = new ArrayList<>();
                for (String m : p.getMethods() == null ? List.<String>of() : p.getMethods()) {
                    String code = sources.getMethodSourceCode(p.getClassName(), m);
                    if (code == null || code.isBlank()) {
                        missingCode++;
                    }
                    Map<String, Object> method = new LinkedHashMap<>();
                    method.put("name", m);
                    method.put("label", HomeController.methodLabel(m));
                    method.put("code", code == null ? "" : code);
                    methods.add(method);
                }
                List<Map<String, Object>> related = new ArrayList<>();
                for (ProblemInfo other : problems) {
                    if (!other.getName().equals(p.getName()) && related.size() < 4) {
                        // LinkedHashMap, not Map.of: a stable key order keeps re-exports from changing every file
                        Map<String, Object> r = new LinkedHashMap<>();
                        r.put("name", other.getName());
                        r.put("slug", slugs.get(other.getName()));
                        r.put("shortDescription", String.valueOf(other.getShortDescription()));
                        related.add(r);
                    }
                }
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("name", p.getName());
                detail.put("slug", slugs.get(p.getName()));
                detail.put("category", p.getCategory());
                detail.put("description", p.getDescription());
                detail.put("shortDescription", p.getShortDescription());
                detail.put("className", p.getClassName());
                detail.put("methods", methods);
                detail.put("paramNames", p.getParamNames());
                detail.put("paramTypes", p.getParamTypes());
                detail.put("exampleInputs", p.getExampleInputs());
                detail.put("related", related);
                json.writeValue(problemsDir.resolve(slugs.get(p.getName()) + ".json").toFile(), detail);
            }
            Map<String, Object> c = new LinkedHashMap<>();
            c.put("name", category);
            c.put("problems", summaries);
            categories.add(c);
        }

        Map<String, Object> catalog = new LinkedHashMap<>();
        catalog.put("problemCount", slugs.size());
        catalog.put("categories", categories);
        json.writeValue(out.resolve("catalog.json").toFile(), catalog);
        System.out.printf("Exported %d problems in %d categories to %s (%d methods without source)%n",
                slugs.size(), categories.size(), out.toAbsolutePath(), missingCode);
    }

    static String slug(String name) {
        String s = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return s.isEmpty() ? "problem" : s;
    }

    private StaticDataExport() {}
}
