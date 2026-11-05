package com.algorithm.web.service;

import com.algorithm.web.export.StaticDataExport;
import com.algorithm.web.model.ProblemInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Guards the catalogue the site is built from: methods that exist, explanations that match them. */
class ProblemCatalogIntegrityTest {

    private static final Path EXPLANATIONS = Path.of("src/main/resources/explanations");
    private static final Pattern BIG_O = Pattern.compile("^O\\(.+\\)$");
    private static final Set<String> JAVA_VERSIONS = Set.of("any", "5", "7", "8", "9", "10", "11", "14", "16", "17", "21");

    private final ObjectMapper mapper = new ObjectMapper();
    private ProblemRegistryService registry;

    @BeforeEach
    void setUp() {
        registry = new ProblemRegistryService();
        registry.initialize();
    }

    @Test
    void everyListedMethodExistsAsAPublicStaticMethod() throws Exception {
        List<String> problems = new ArrayList<>();
        for (ProblemInfo p : registry.getAllProblems()) {
            Class<?> type = Class.forName(p.getClassName());
            for (String name : p.getMethods()) {
                boolean found = false;
                for (Method m : type.getMethods()) {
                    if (m.getName().equals(name) && Modifier.isStatic(m.getModifiers())) {
                        found = true;
                    }
                }
                if (!found) {
                    problems.add(p.getName() + ": " + p.getClassName() + "." + name);
                }
            }
        }
        assertTrue(problems.isEmpty(), "Registered methods missing from their class: " + problems);
    }

    @Test
    void explanationsMatchTheirProblemsMethods() throws IOException {
        Map<String, ProblemInfo> bySlug = new HashMap<>();
        for (ProblemInfo p : registry.getAllProblems()) {
            bySlug.put(StaticDataExport.slug(p.getName()), p);
        }
        List<String> errors = new ArrayList<>();
        try (Stream<Path> files = Files.list(EXPLANATIONS)) {
            for (Path file : files.filter(f -> f.toString().endsWith(".json")).toList()) {
                String slug = file.getFileName().toString().replace(".json", "");
                ProblemInfo problem = bySlug.get(slug);
                if (problem == null) {
                    errors.add(slug + ": no problem with this slug");
                    continue;
                }
                JsonNode e = mapper.readTree(file.toFile());
                requireText(e, "idea", slug, errors);
                requireText(e, "pattern", slug, errors);
                JsonNode methods = e.path("methods");
                Set<String> explained = new HashSet<>();
                methods.fieldNames().forEachRemaining(explained::add);
                if (!explained.equals(new HashSet<>(problem.getMethods()))) {
                    errors.add(slug + ": explains " + explained + " but the problem has " + problem.getMethods());
                }
                if (!problem.getMethods().contains(e.path("recommended").asText())) {
                    errors.add(slug + ": recommended must be one of its methods");
                }
                for (Iterator<Map.Entry<String, JsonNode>> it = methods.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> m = it.next();
                    String where = slug + "." + m.getKey();
                    requireText(m.getValue(), "summary", where, errors);
                    if (!m.getValue().path("steps").isArray() || m.getValue().path("steps").isEmpty()) {
                        errors.add(where + ": needs steps");
                    }
                    for (String field : List.of("time", "space")) {
                        if (!BIG_O.matcher(m.getValue().path(field).asText()).matches()) {
                            errors.add(where + ": " + field + " must look like O(...)");
                        }
                    }
                    if (!JAVA_VERSIONS.contains(m.getValue().path("java").asText())) {
                        errors.add(where + ": java must be one of " + JAVA_VERSIONS);
                    }
                }
            }
        }
        assertTrue(errors.isEmpty(), String.join("\n", errors));
    }

    @Test
    void exportedProblemCarriesItsExplanation() throws IOException {
        try (InputStream in = StaticDataExport.class.getResourceAsStream("/explanations/two-sum.json")) {
            assertNotNull(in, "explanations are on the classpath for the exporter");
        }
    }

    private static void requireText(JsonNode node, String field, String where, List<String> errors) {
        if (node.path(field).asText("").isBlank()) {
            errors.add(where + ": missing " + field);
        }
    }
}
