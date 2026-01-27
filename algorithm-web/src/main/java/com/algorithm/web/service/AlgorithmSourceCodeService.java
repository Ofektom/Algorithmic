package com.algorithm.web.service;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlgorithmSourceCodeService {

    public String getMethodSourceCode(String className, String methodName) {
        try {
            String[] possiblePaths = {
                    "src/" + className.replace('.', '/') + ".java",
                    "../src/" + className.replace('.', '/') + ".java",
                    "../../src/" + className.replace('.', '/') + ".java",
                    "algorithm-core/src/main/java/" + className.replace('.', '/') + ".java",
                    "../algorithm-core/src/main/java/" + className.replace('.', '/') + ".java"
            };

            for (String filePath : possiblePaths) {
                Path sourcePath = Paths.get(filePath);
                if (Files.exists(sourcePath) && Files.isRegularFile(sourcePath)) {
                    String code = extractMethodFromFile(sourcePath, methodName);
                    if (code != null && !code.trim().isEmpty()) {
                        return trimCommonIndent(code);
                    }
                }
            }

            String resourcePath = className.replace('.', '/') + ".java";
            Resource resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                String code = extractMethodFromResource(resource, methodName);
                if (code != null && !code.trim().isEmpty()) {
                    return trimCommonIndent(code);
                }
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractMethodFromFile(Path filePath, String methodName) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            return extractMethod(lines, methodName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractMethodFromResource(Resource resource, String methodName) {
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            return extractMethod(lines, methodName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractMethod(List<String> lines, String methodName) {
        boolean inMethod = false;
        int braceCount = 0;
        List<String> methodLines = new ArrayList<>();
        boolean foundMethodSignature = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String trimmed = line.trim();

            if (!inMethod && trimmed.contains(methodName + "(")) {
                if (trimmed.contains("public") || trimmed.contains("private") ||
                        trimmed.contains("protected") || trimmed.contains("static")) {
                    inMethod = true;
                    foundMethodSignature = true;
                    methodLines.add(line);

                    braceCount += countBraces(line);

                    if (braceCount == 0 && trimmed.contains("}")) {
                        break;
                    }
                    continue;
                }
            }

            if (inMethod) {
                methodLines.add(line);
                braceCount += countBraces(line);

                if (braceCount == 0) {
                    break;
                }
            }
        }

        if (!foundMethodSignature || methodLines.isEmpty()) {
            return null;
        }

        return String.join("\n", methodLines);
    }

    /**
     * Improved: Dedent method body while preserving signature indentation
     */
    /**
     * Removes common leading indentation from all lines (including signature if
     * needed)
     * and trims blank lines. Much more aggressive than before.
     */
    private String trimCommonIndent(String code) {
        if (code == null || code.isEmpty()) {
            return code;
        }

        // Split into lines
        String[] rawLines = code.split("\n");
        List<String> lines = new ArrayList<>();

        // Skip leading blank lines
        boolean started = false;
        for (String line : rawLines) {
            if (!started && line.trim().isEmpty())
                continue;
            started = true;
            lines.add(line);
        }

        // Remove trailing blank lines
        while (!lines.isEmpty() && lines.get(lines.size() - 1).trim().isEmpty()) {
            lines.remove(lines.size() - 1);
        }

        if (lines.isEmpty()) {
            return "";
        }

        // Find minimum indentation across ALL non-empty lines
        int minIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                int indent = 0;
                while (indent < line.length() && Character.isWhitespace(line.charAt(indent))) {
                    indent++;
                }
                minIndent = Math.min(minIndent, indent);
            }
        }

        // If no indent at all → just trim blank lines
        if (minIndent == 0 || minIndent == Integer.MAX_VALUE) {
            return String.join("\n", lines).trim();
        }

        // Dedent EVERY line by minIndent
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.length() >= minIndent) {
                sb.append(line.substring(minIndent));
            } else {
                sb.append(line); // very short line
            }
            sb.append("\n");
        }

        // Final trim
        return sb.toString().trim();
    }

    private int countBraces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == '{')
                count++;
            if (c == '}')
                count--;
        }
        return count;
    }
}