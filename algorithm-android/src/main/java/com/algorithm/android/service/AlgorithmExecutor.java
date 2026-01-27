package com.algorithm.android.service;

import android.content.Context;
import com.algorithm.android.data.ProblemInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to execute algorithms dynamically using reflection
 * This mirrors the execution logic from console Main.java
 */
public class AlgorithmExecutor {
    
    private static AlgorithmExecutor instance;
    private Context context;
    
    private AlgorithmExecutor() {}
    
    /**
     * Set Android context for reading assets
     */
    public void setContext(Context context) {
        this.context = context;
    }
    
    public static AlgorithmExecutor getInstance() {
        if (instance == null) {
            instance = new AlgorithmExecutor();
        }
        return instance;
    }
    
    /**
     * Execution result containing the result and metadata
     */
    public static class ExecutionResult {
        public Object result;
        public long executionTimeMs;
        public String error;
        public boolean success;
        
        public ExecutionResult(Object result, long executionTimeMs) {
            this.result = result;
            this.executionTimeMs = executionTimeMs;
            this.success = true;
        }
        
        public ExecutionResult(String error) {
            this.error = error;
            this.success = false;
            this.executionTimeMs = 0;
        }
    }
    
    /**
     * Execute an algorithm method
     * @param problem The problem info
     * @param methodName The method name to execute
     * @param inputs The input parameters
     * @return Execution result with result and execution time
     */
    public ExecutionResult execute(ProblemInfo problem, String methodName, Object[] inputs) {
        try {
            // Load the class
            Class<?> clazz = Class.forName(problem.className);
            
            // Find the method
            Method method = findMethod(clazz, methodName, problem.paramTypes);
            
            if (method == null) {
                return new ExecutionResult("Method not found: " + methodName);
            }
            
            // Execute the method
            long startTime = System.nanoTime();
            Object result = method.invoke(null, inputs);
            long endTime = System.nanoTime();
            
            long executionTimeMs = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            
            return new ExecutionResult(result, executionTimeMs);
            
        } catch (ClassNotFoundException e) {
            return new ExecutionResult("Class not found: " + problem.className);
        } catch (Exception e) {
            return new ExecutionResult("Error executing method: " + e.getMessage());
        }
    }
    
    /**
     * Find a method by name and parameter types
     */
    private Method findMethod(Class<?> clazz, String methodName, String[] paramTypes) {
        Method[] methods = clazz.getMethods();
        
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] params = method.getParameterTypes();
                
                if (params.length == paramTypes.length) {
                    // Check if parameter types match
                    boolean match = true;
                    for (int i = 0; i < params.length; i++) {
                        if (!isTypeCompatible(params[i], paramTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    
                    if (match) {
                        return method;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Check if a Java type is compatible with the string type representation
     */
    private boolean isTypeCompatible(Class<?> javaType, String typeString) {
        // Handle primitive types and arrays
        if (typeString.equals("int[]")) {
            return javaType == int[].class;
        } else if (typeString.equals("int")) {
            return javaType == int.class || javaType == Integer.class;
        } else if (typeString.equals("String")) {
            return javaType == String.class;
        } else if (typeString.equals("String[]")) {
            return javaType == String[].class;
        } else if (typeString.equals("char[]")) {
            return javaType == char[].class;
        } else if (typeString.equals("long")) {
            return javaType == long.class || javaType == Long.class;
        } else if (typeString.equals("boolean")) {
            return javaType == boolean.class || javaType == Boolean.class;
        } else if (typeString.contains("List")) {
            // For List types, check if it's assignable
            return javaType.getName().contains("List");
        } else if (typeString.equals("TreeNode")) {
            // TreeNode is a custom class in trees package
            return javaType.getSimpleName().equals("TreeNode");
        } else if (typeString.equals("ListNode")) {
            // ListNode is a custom class in linkedlist package
            return javaType.getSimpleName().equals("ListNode");
        }
        
        // Fallback: check if the simple name matches
        return javaType.getSimpleName().equals(getJavaTypeName(typeString));
    }
    
    /**
     * Get Java type name from string representation
     */
    private String getJavaTypeName(String type) {
        switch (type) {
            case "int[]":
                return "int[]";
            case "String":
                return "String";
            case "String[]":
                return "String[]";
            case "int":
                return "int";
            case "char[]":
                return "char[]";
            default:
                return type;
        }
    }
    
    /**
     * Format execution result as string for display
     */
    public String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        if (result instanceof int[]) {
            return java.util.Arrays.toString((int[]) result);
        } else if (result instanceof String[]) {
            return java.util.Arrays.toString((String[]) result);
        } else if (result instanceof char[]) {
            return java.util.Arrays.toString((char[]) result);
        } else if (result instanceof boolean[]) {
            return java.util.Arrays.toString((boolean[]) result);
        } else if (result instanceof java.util.List) {
            return result.toString();
        } else if (result instanceof java.util.Map) {
            return result.toString();
        } else if (result.getClass().isArray()) {
            // Handle other array types
            return java.util.Arrays.deepToString(new Object[]{result});
        }
        
        return result.toString();
    }
    
    /**
     * Format input value as string for display
     */
    public String formatInputValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        if (value instanceof int[]) {
            return java.util.Arrays.toString((int[]) value);
        } else if (value instanceof String[]) {
            return java.util.Arrays.toString((String[]) value);
        } else if (value instanceof char[]) {
            return java.util.Arrays.toString((char[]) value);
        } else if (value instanceof java.util.List) {
            return value.toString();
        }
        
        return value.toString();
    }
    
    /**
     * Get source code for a method
     * Tries to read from assets, falls back to helpful message
     */
    public String getMethodSourceCode(String className, String methodName) {
        // Try reading from assets first
        String sourceCode = readFromAssets(className, methodName);
        if (sourceCode != null && !sourceCode.isEmpty()) {
            return sourceCode;
        }
        
        // Fallback: Provide helpful message with file location
        String filePath = className.replace(".", "/") + ".java";
        return "// Source code for " + className + "." + methodName + "\n" +
               "// \n" +
               "// To view the source code:\n" +
               "// 1. Open the project in your IDE\n" +
               "// 2. Navigate to: algorithm-core/src/main/java/" + filePath + "\n" +
               "// 3. Find method: " + methodName + "\n" +
               "// \n" +
               "// Note: Source code can be bundled in assets for runtime viewing.\n" +
               "// File location: algorithm-core/src/main/java/" + filePath;
    }
    
    /**
     * Try to read source code from assets
     */
    private String readFromAssets(String className, String methodName) {
        try {
            // Convert className to file path (e.g., "strings.IsAnagram" -> "strings/IsAnagram.java")
            String filePath = "source/" + className.replace(".", "/") + ".java";
            
            // Try to read from assets
            Context context = getContext();
            if (context != null) {
                InputStream inputStream = context.getAssets().open(filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
                
                // Extract the method code
                return extractMethodCode(lines, methodName);
            }
        } catch (IOException e) {
            // Assets file not found - this is expected if source files aren't bundled
            // Return null to use fallback message
        }
        return null;
    }
    
    /**
     * Extract method code from file lines (similar to console implementation)
     */
    private String extractMethodCode(List<String> lines, String methodName) {
        StringBuilder code = new StringBuilder();
        boolean inMethod = false;
        int braceCount = 0;
        int methodStart = -1;
        
        // Find the method signature
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            
            // Look for method signature (public static ... methodName)
            if (line.contains("public static") && line.contains(methodName + "(")) {
                methodStart = i;
                inMethod = true;
                
                // Add method comment if it exists (lines before the method)
                int commentStart = i - 1;
                while (commentStart >= 0 && 
                       (lines.get(commentStart).trim().startsWith("/**") || 
                        lines.get(commentStart).trim().startsWith("*") ||
                        lines.get(commentStart).trim().isEmpty())) {
                    if (lines.get(commentStart).trim().startsWith("/**")) {
                        commentStart--;
                        break;
                    }
                    commentStart--;
                }
                if (commentStart >= 0 && commentStart < i) {
                    for (int j = commentStart + 1; j < i; j++) {
                        code.append(lines.get(j)).append("\n");
                    }
                }
                
                // Count braces in the signature line
                braceCount += countBraces(line);
                code.append(line).append("\n");
                
                if (braceCount == 0) {
                    // Method on single line - continue
                    continue;
                }
            } else if (inMethod) {
                // We're inside the method, add line and count braces
                braceCount += countBraces(line);
                code.append(line).append("\n");
                
                // Method ends when brace count reaches 0
                if (braceCount == 0) {
                    break;
                }
            }
        }
        
        return methodStart >= 0 ? code.toString() : null;
    }
    
    /**
     * Count the difference between opening and closing braces in a line
     */
    private int countBraces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == '{') count++;
            if (c == '}') count--;
        }
        return count;
    }
    
    /**
     * Get Android context
     */
    private Context getContext() {
        return context;
    }
}

