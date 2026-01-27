import java.util.*;
import java.util.Scanner;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import arrays.*;
import strings.*;
import trees.*;
import linkedlist.*;
import pointers.*;
import window.*;
import stack.*;
import search.*;
import dp.*;
import graph.*;
import heap.*;
import backtrack.*;
import trie.*;
import intervals.*;
import math.*;
import greedy.*;
import bitmanip.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   ALGORITHM TESTING SYSTEM");
        System.out.println("========================================\n");

        while (true) {
            int category = showCategoryMenu();
            if (category == 0) {
                System.out.println("Thank you for using Algorithm Testing System!");
                break;
            }
            runCategory(category);
        }
        scanner.close();
    }

    private static int showCategoryMenu() {
        System.out.println("\nSelect Category:");
        System.out.println("1. Arrays & Hashing");
        System.out.println("2. Strings");
        System.out.println("3. Trees");
        System.out.println("4. Linked List");
        System.out.println("5. Two Pointers");
        System.out.println("6. Sliding Window");
        System.out.println("7. Stack");
        System.out.println("8. Search");
        System.out.println("9. Dynamic Programming");
        System.out.println("10. Graphs");
        System.out.println("11. Heap/Priority Queue");
        System.out.println("12. Backtracking");
        System.out.println("13. Trie");
        System.out.println("14. Intervals");
        System.out.println("15. Math/Geometry");
        System.out.println("16. Greedy");
        System.out.println("17. Bit Manipulation");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void runCategory(int category) {
        ProblemInfo[] problems = getProblemsForCategory(category);
        if (problems == null || problems.length == 0) {
            System.out.println("Category not available.");
            return;
        }

        while (true) {
            int problemChoice = showProblemMenu(problems);
            if (problemChoice == 0) {
                break; // Go back to category menu
            }
            if (problemChoice > 0 && problemChoice <= problems.length) {
                runProblem(problems[problemChoice - 1]);
            }
        }
    }

    private static int showProblemMenu(ProblemInfo[] problems) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Select Problem:");
        for (int i = 0; i < problems.length; i++) {
            System.out.println((i + 1) + ". " + problems[i].name);
        }
        System.out.println("0. Back to Categories");
        System.out.print("Enter choice: ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void runProblem(ProblemInfo problem) {
        while (true) {
            int methodChoice = showMethodMenu(problem);
            if (methodChoice == 0) {
                break; // Go back to problem menu
            }
            if (methodChoice > 0 && methodChoice <= problem.methods.length) {
                executeMethod(problem, problem.methods[methodChoice - 1]);
            }
        }
    }

    private static int showMethodMenu(ProblemInfo problem) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Problem: " + problem.name);
        System.out.println("=".repeat(40));
        System.out.println("Available Methods:");
        for (int i = 0; i < problem.methods.length; i++) {
            System.out.println((i + 1) + ". " + problem.methods[i]);
        }
        System.out.println("0. Back to Problems");
        System.out.print("Enter method number: ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void executeMethod(ProblemInfo problem, String methodName) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Problem: " + problem.name);
        System.out.println("Method: " + methodName);
        System.out.println("=".repeat(40));

        try {
            // Show input hints
            showInputHints(problem);

            // Get input choice
            System.out.print("\n1. Use example input\n2. Provide custom input\nEnter choice (1-2): ");
            int inputChoice = Integer.parseInt(scanner.nextLine().trim());

            Object[] inputs;
            if (inputChoice == 1) {
                inputs = problem.exampleInputs;
                System.out.println("\nUsing example input:");
                printInputs(problem, inputs);
            } else {
                inputs = getCustomInputs(problem);
            }

            // Execute method using reflection
            Class<?> clazz = Class.forName(problem.className);
            Method method = findMethod(clazz, methodName, problem.paramTypes);
            
            if (method == null) {
                System.out.println("Error: Method not found!");
                return;
            }

            // Show inputs being processed
            System.out.println("\n" + "━".repeat(40));
            System.out.println("⚙️  Processing Inputs:");
            for (int i = 0; i < problem.paramNames.length; i++) {
                System.out.println("   " + problem.paramNames[i] + " = " + formatInputValue(inputs[i]));
            }
            
            System.out.println("\n🔄 Executing Method: " + methodName);
            System.out.print("   Processing");
            // Small delay to show processing (optional visual feedback)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }
            System.out.print("... ");
            
            long startTime = System.nanoTime();
            Object result = method.invoke(null, inputs);
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds

            System.out.println("✓ Done!\n");
            System.out.println("━".repeat(40));
            System.out.println("📊 RESULT:");
            System.out.println("━".repeat(40));
            System.out.println("   " + formatResult(result));
            System.out.println("━".repeat(40));
            System.out.println("⏱️  Execution Time: " + String.format("%.4f", executionTime) + " ms");
            
            // Display algorithm code
            System.out.println("\n" + "━".repeat(40));
            System.out.println("💻 ALGORITHM CODE:");
            System.out.println("━".repeat(40));
            String algorithmCode = getMethodSourceCode(problem.className, methodName);
            if (algorithmCode != null) {
                System.out.println(algorithmCode);
            } else {
                System.out.println("   (Source code not found)");
            }
            System.out.println("━".repeat(40));
            System.out.println("=".repeat(40));

        } catch (Exception e) {
            System.out.println("Error executing method: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void showInputHints(ProblemInfo problem) {
        System.out.println("\nInput Parameters Required:");
        System.out.println("━".repeat(38));
        for (int i = 0; i < problem.paramNames.length; i++) {
            System.out.println((i + 1) + ". " + problem.paramNames[i] + " (" + problem.paramTypes[i] + ")");
            System.out.println("   💡 " + getInputHint(problem.paramTypes[i]));
            if (problem.exampleInputs != null && i < problem.exampleInputs.length) {
                System.out.println("   📝 Example: " + formatInputValue(problem.exampleInputs[i]));
            }
            System.out.println();
        }
        System.out.println("━".repeat(38));
    }

    private static String getInputHint(String type) {
        switch (type) {
            case "int[]":
                return "Format: [1,2,3] or 1,2,3 or 1 2 3";
            case "String":
                return "Format: Enter text directly (quotes optional), e.g., \"hello\" or hello";
            case "String[]":
                return "Format: [\"a\",\"b\"] or a,b,c or a b c";
            case "int":
                return "Format: Enter number directly, e.g., 9";
            case "char[]":
                return "Format: ['a','b','c'] or abc or a,b,c";
            default:
                return "Enter value as required";
        }
    }

    private static Object[] getCustomInputs(ProblemInfo problem) {
        Object[] inputs = new Object[problem.paramNames.length];
        
        for (int i = 0; i < problem.paramNames.length; i++) {
            System.out.print("\nEnter value for '" + problem.paramNames[i] + "' (" + problem.paramTypes[i] + "):\n> ");
            String input = scanner.nextLine().trim();
            inputs[i] = parseInput(input, problem.paramTypes[i]);
        }
        
        return inputs;
    }

    private static Object parseInput(String input, String type) {
        switch (type) {
            case "int[]":
                return parseIntArray(input);
            case "String":
                return parseString(input);
            case "String[]":
                return parseStringArray(input);
            case "int":
                return Integer.parseInt(input.trim());
            case "char[]":
                return parseCharArray(input);
            default:
                return input;
        }
    }

    private static int[] parseIntArray(String input) {
        input = input.trim();
        // Remove brackets if present
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1);
        }
        
        String[] parts = input.split("[, ]+");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    private static String parseString(String input) {
        input = input.trim();
        // Remove quotes if present
        if ((input.startsWith("\"") && input.endsWith("\"")) ||
            (input.startsWith("'") && input.endsWith("'"))) {
            input = input.substring(1, input.length() - 1);
        }
        return input;
    }

    private static String[] parseStringArray(String input) {
        input = input.trim();
        // Remove brackets if present
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1);
            // Handle quoted strings in brackets
            List<String> list = new ArrayList<>();
            String[] parts = input.split(",\\s*");
            for (String part : parts) {
                part = part.trim();
                if ((part.startsWith("\"") && part.endsWith("\"")) ||
                    (part.startsWith("'") && part.endsWith("'"))) {
                    part = part.substring(1, part.length() - 1);
                }
                list.add(part);
            }
            return list.toArray(new String[0]);
        } else {
            // Simple comma or space separated
            return input.split("[, ]+");
        }
    }

    private static char[] parseCharArray(String input) {
        input = input.trim();
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1);
        }
        return input.replaceAll("['\", ]", "").toCharArray();
    }

    private static void printInputs(ProblemInfo problem, Object[] inputs) {
        for (int i = 0; i < problem.paramNames.length; i++) {
            System.out.println("  " + problem.paramNames[i] + " = " + formatInputValue(inputs[i]));
        }
    }

    private static String formatInputValue(Object value) {
        if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        } else if (value instanceof String[]) {
            return Arrays.toString((String[]) value);
        } else if (value instanceof char[]) {
            return Arrays.toString((char[]) value);
        }
        return String.valueOf(value);
    }

    private static String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof int[]) {
            return Arrays.toString((int[]) result);
        } else if (result instanceof Boolean) {
            return String.valueOf(result);
        } else if (result instanceof List) {
            return result.toString();
        }
        return result.toString();
    }

    private static Method findMethod(Class<?> clazz, String methodName, String[] paramTypes) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == paramTypes.length) {
                    // Simple type checking - for exact match
                    boolean match = true;
                    for (int i = 0; i < params.length; i++) {
                        if (!params[i].getSimpleName().equals(getJavaTypeName(paramTypes[i]))) {
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

    private static String getJavaTypeName(String type) {
        return switch (type) {
            case "int[]" -> "int[]";
            case "String" -> "String";
            case "String[]" -> "String[]";
            case "int" -> "int";
            case "char[]" -> "char[]";
            default -> type;
        };
    }

    /**
     * Get the source code of a specific method from the class file
     */
    private static String getMethodSourceCode(String className, String methodName) {
        try {
            // Convert className like "strings.IsAnagram" to file path
            // Try multiple possible locations
            String[] possiblePaths = {
                "algorithm-core/src/main/java/" + className.replace(".", "/") + ".java",
                "../algorithm-core/src/main/java/" + className.replace(".", "/") + ".java",
                "src/" + className.replace(".", "/") + ".java",
                "../src/" + className.replace(".", "/") + ".java"
            };
            
            Path path = null;
            for (String filePath : possiblePaths) {
                Path testPath = Paths.get(filePath);
                if (Files.exists(testPath)) {
                    path = testPath;
                    break;
                }
            }
            
            if (path == null) {
                return null;
            }
            
            // Read all lines from the file
            List<String> lines = Files.readAllLines(path);
            
            // Find the method and extract its code
            return extractMethodCode(lines, methodName);
            
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Extract a specific method's source code from file lines
     */
    private static String extractMethodCode(List<String> lines, String methodName) {
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
    private static int countBraces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == '{') count++;
            if (c == '}') count--;
        }
        return count;
    }

    // Problem Registry - Enhanced with descriptions and keywords
    static class ProblemInfo {
        String name;
        String[] aliases;              // Alternative names for matching
        String description;            // Full problem statement
        String shortDescription;       // Brief summary
        String[] keywords;             // Keywords for search/matching
        String category;
        String className;
        String[] methods;
        String[] paramNames;
        String[] paramTypes;
        Object[] exampleInputs;

        // Full constructor with all fields
        ProblemInfo(String name, String[] aliases, String description,
                   String shortDescription, String[] keywords, String category,
                   String className, String[] methods, 
                   String[] paramNames, String[] paramTypes, Object[] exampleInputs) {
            this.name = name;
            this.aliases = aliases != null ? aliases : new String[]{};
            this.description = description != null ? description : "";
            this.shortDescription = shortDescription != null ? shortDescription : "";
            this.keywords = keywords != null ? keywords : new String[]{};
            this.category = category != null ? category : "";
            this.className = className;
            this.methods = methods;
            this.paramNames = paramNames;
            this.paramTypes = paramTypes;
            this.exampleInputs = exampleInputs;
        }

        // Backward compatible constructor
        ProblemInfo(String name, String className, String[] methods, 
                   String[] paramNames, String[] paramTypes, Object[] exampleInputs) {
            this(name, new String[]{}, "", "", new String[]{}, "", 
                 className, methods, paramNames, paramTypes, exampleInputs);
        }
    }

    private static ProblemInfo[] getProblemsForCategory(int category) {
        switch (category) {
            case 1: // Arrays & Hashing
                return new ProblemInfo[] {
                    new ProblemInfo("Two Sum", 
                        new String[]{"TwoSum", "2Sum"},
                        "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume that each input would have exactly one solution, and you may not use the same element twice. You can return the answer in any order.",
                        "Find two numbers that add up to target",
                        new String[]{"array", "integers", "target", "indices", "add", "sum", "two", "numbers"},
                        "Arrays & Hashing",
                        "arrays.TwoSum",
                        new String[]{"computeWithHashMap", "computeWithHashMapTwoPass", "computeWithBruteForce"},
                        new String[]{"nums", "target"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{2,7,11,15}, 9}),
                    
                    new ProblemInfo("Contains Duplicate",
                        new String[]{"ContainsDuplicate", "Duplicate"},
                        "Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.",
                        "Check if array has duplicate values",
                        new String[]{"array", "duplicate", "appears", "twice", "distinct"},
                        "Arrays & Hashing",
                        "arrays.ContainsDuplicate",
                        new String[]{"computeWithHashSet", "computeWithStream", "computeWithSorting", "computeWithBruteForce"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,2,3,1}}),
                    
                    new ProblemInfo("Missing Number",
                        new String[]{"MissingNumber", "Missing"},
                        "Given an array nums containing n distinct numbers in the range [0, n], return the only number in the range that is missing from the array.",
                        "Find the missing number in array [0, n]",
                        new String[]{"array", "missing", "number", "range", "distinct"},
                        "Arrays & Hashing",
                        "arrays.MissingNumber",
                        new String[]{"computeWithHashSet", "computeWithMath", "computeWithXOR", "computeWithSorting"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{3,0,1}}),
                    
                    new ProblemInfo("Single Number",
                        new String[]{"SingleNumber", "Single"},
                        "Given a non-empty array of integers nums, every element appears twice except for one. Find that single one.",
                        "Find the number that appears only once",
                        new String[]{"array", "single", "appears", "twice", "once", "unique"},
                        "Arrays & Hashing",
                        "arrays.SingleNumber",
                        new String[]{"computeWithXOR", "computeWithHashSet", "computeWithHashMap"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{2,2,1}}),
                    
                    new ProblemInfo("Plus One",
                        new String[]{"PlusOne", "Increment Array"},
                        "You are given a large integer represented as an integer array digits, where each digits[i] is the ith digit of the integer. Increment the large integer by one and return the resulting array of digits.",
                        "Increment a number represented as array by one",
                        new String[]{"array", "digits", "increment", "plus", "one", "large", "integer"},
                        "Arrays & Hashing",
                        "arrays.PlusOne",
                        new String[]{"computeIterative", "computeWithArrayList", "computeWithRecursion"},
                        new String[]{"digits"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,2,3}}),
                    
                    new ProblemInfo("Count Even Numbers",
                        new String[]{"CountEven", "Count Even"},
                        "Given an array of integers, count how many numbers are even.",
                        "Count even numbers in array",
                        new String[]{"array", "count", "even", "numbers", "integers"},
                        "Arrays & Hashing",
                        "arrays.CountEven",
                        new String[]{"compute", "computeWithStream", "computeWithRecursion", "computeWithWhile"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,2,3,4,5,6}}),
                    
                    new ProblemInfo("Product of Array Except Self",
                        new String[]{"ProductExceptSelf", "Array Product"},
                        "Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i]. You must write an algorithm that runs in O(n) time and without using the division operator.",
                        "Return array where each element is product of all others",
                        new String[]{"array", "product", "except", "self", "prefix", "suffix"},
                        "Arrays & Hashing",
                        "arrays.ProductExceptSelf",
                        new String[]{"computeWithPrefixSuffix", "computeWithOptimizedSpace", "computeWithRecursion"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,2,3,4}}),
                    
                    new ProblemInfo("Top K Frequent Elements",
                        new String[]{"TopKFrequent", "K Most Frequent", "Top K"},
                        "Given an integer array nums and an integer k, return the k most frequent elements. You may return the answer in any order.",
                        "Find k most frequent elements in array",
                        new String[]{"array", "frequent", "top", "k", "frequency", "elements", "most"},
                        "Arrays & Hashing",
                        "arrays.TopKFrequent",
                        new String[]{"computeWithHeap", "computeWithBucketSort", "computeWithSorting"},
                        new String[]{"nums", "k"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{1,1,1,2,2,3}, 2}),
                    
                    new ProblemInfo("Longest Consecutive Sequence",
                        new String[]{"LongestConsecutive", "Consecutive Sequence"},
                        "Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence. You must write an algorithm that runs in O(n) time.",
                        "Find length of longest consecutive sequence",
                        new String[]{"array", "consecutive", "sequence", "longest", "unsorted"},
                        "Arrays & Hashing",
                        "arrays.LongestConsecutive",
                        new String[]{"computeWithHashSet", "computeWithRecursion", "computeWithSorting"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{100,4,200,1,3,2}}),
                    
                    new ProblemInfo("Maximum Subarray",
                        new String[]{"MaximumSubarray", "Max Subarray", "Kadane"},
                        "Given an integer array nums, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum. A subarray is a contiguous part of an array.",
                        "Find maximum sum of contiguous subarray",
                        new String[]{"array", "subarray", "maximum", "sum", "contiguous", "kadane"},
                        "Arrays & Hashing",
                        "arrays.MaximumSubarray",
                        new String[]{"computeWithKadane", "computeWithDP", "computeWithDivideConquer", "computeWithBruteForce"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{-2,1,-3,4,-1,2,1,-5,4}}),
                    
                    new ProblemInfo("Maximum Product Subarray",
                        new String[]{"MaximumProductSubarray", "Max Product Subarray", "Product Subarray"},
                        "Given an integer array nums, find a contiguous non-empty subarray within the array that has the largest product, and return the product. The test cases are generated so that the answer will fit in a 32-bit integer.",
                        "Find maximum product of contiguous subarray",
                        new String[]{"array", "subarray", "maximum", "product", "contiguous"},
                        "Arrays & Hashing",
                        "arrays.MaximumProductSubarray",
                        new String[]{"computeWithDP", "computeWithTwoPasses", "computeWithBruteForce", "computeWithRecursion"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{2,3,-2,4}}),
                    
                    new ProblemInfo("Contains Duplicate II",
                        new String[]{"ContainsDuplicateII", "Duplicate II", "Duplicate Distance"},
                        "Given an integer array nums and an integer k, return true if there are two distinct indices i and j in the array such that nums[i] == nums[j] and abs(i - j) <= k.",
                        "Check if duplicate exists within k distance",
                        new String[]{"array", "duplicate", "distance", "k", "indices", "hashmap", "sliding window"},
                        "Arrays & Hashing",
                        "arrays.ContainsDuplicateII",
                        new String[]{"computeWithHashMap", "computeWithHashSet", "computeWithBruteForce"},
                        new String[]{"nums", "k"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{1,2,3,1}, 3}),
                    
                    new ProblemInfo("Rotate Array",
                        new String[]{"RotateArray", "Rotate", "Shift Array"},
                        "Given an integer array nums, rotate the array to the right by k steps, where k is non-negative.",
                        "Rotate array to the right by k steps",
                        new String[]{"array", "rotate", "shift", "k", "steps", "right"},
                        "Arrays & Hashing",
                        "arrays.RotateArray",
                        new String[]{"computeWithExtraArray", "computeWithReverse", "computeWithArrayList", "computeWithCyclic"},
                        new String[]{"nums", "k"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{1,2,3,4,5,6,7}, 3})
                };
            
            case 2: // Strings
                return new ProblemInfo[] {
                    new ProblemInfo("Valid Anagram",
                        new String[]{"IsAnagram", "Anagram", "Valid Anagram"},
                        "Given two strings s and t, return true if t is an anagram of s, and false otherwise. An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase.",
                        "Check if two strings are anagrams",
                        new String[]{"string", "anagram", "rearrange", "letters", "characters"},
                        "Strings",
                        "strings.IsAnagram",
                        new String[]{"computeWithSorting", "computeWithHashMap", "computeWithArrayCounter", "computeWithStringBuilder"},
                        new String[]{"s", "t"},
                        new String[]{"String", "String"},
                        new Object[]{"anagram", "nagaram"}),
                    
                    new ProblemInfo("Group Anagrams",
                        new String[]{"GroupAnagrams", "Anagram Groups"},
                        "Given an array of strings strs, group the anagrams together. You can return the answer in any order.",
                        "Group strings that are anagrams of each other",
                        new String[]{"string", "array", "anagram", "group", "sort"},
                        "Strings",
                        "strings.GroupAnagrams",
                        new String[]{"computeWithSortedKey", "computeWithStringBuilder", "computeWithCharCount"},
                        new String[]{"strs"},
                        new String[]{"String[]"},
                        new Object[]{new String[]{"eat","tea","tan","ate","nat","bat"}}),
                    
                    new ProblemInfo("Ransom Note",
                        new String[]{"RansomNote", "Ransom"},
                        "Given two strings ransomNote and magazine, return true if ransomNote can be constructed by using the letters from magazine and false otherwise. Each letter in magazine can only be used once in ransomNote.",
                        "Check if ransom note can be constructed from magazine",
                        new String[]{"string", "ransom", "magazine", "construct", "letters"},
                        "Strings",
                        "strings.RansomNote",
                        new String[]{"computeWithHashMap", "computeWithArray", "computeWithStringBuilder"},
                        new String[]{"ransomNote", "magazine"},
                        new String[]{"String", "String"},
                        new Object[]{"aa", "aab"}),
                    
                    new ProblemInfo("Isomorphic Strings",
                        new String[]{"IsomorphicStrings", "Isomorphic"},
                        "Given two strings s and t, determine if they are isomorphic. Two strings s and t are isomorphic if the characters in s can be replaced to get t.",
                        "Check if two strings are isomorphic",
                        new String[]{"string", "isomorphic", "characters", "mapping", "replace"},
                        "Strings",
                        "strings.IsomorphicStrings",
                        new String[]{"computeWithTwoHashMaps", "computeWithHashMapHashSet", "computeWithArray"},
                        new String[]{"s", "t"},
                        new String[]{"String", "String"},
                        new Object[]{"egg", "add"}),
                    
                    new ProblemInfo("Word Pattern",
                        new String[]{"WordPattern", "Pattern"},
                        "Given a pattern and a string s, find if s follows the same pattern. Here follow means a full match, such that there is a bijection between a letter in pattern and a non-empty word in s.",
                        "Check if string follows word pattern",
                        new String[]{"string", "pattern", "word", "bijection", "mapping"},
                        "Strings",
                        "strings.WordPattern",
                        new String[]{"computeWithHashMap", "computeWithTwoHashMaps", "computeWithStringBuilder"},
                        new String[]{"pattern", "s"},
                        new String[]{"String", "String"},
                        new Object[]{"abba", "dog cat cat dog"}),
                    
                    new ProblemInfo("First Unique Character",
                        new String[]{"FirstUniqueCharacter", "First Unique", "Non-repeating"},
                        "Given a string s, find the first non-repeating character in it and return its index. If it does not exist, return -1.",
                        "Find first non-repeating character index",
                        new String[]{"string", "unique", "non-repeating", "character", "index", "first"},
                        "Strings",
                        "strings.FirstUniqueCharacter",
                        new String[]{"computeWithHashMap", "computeWithArray", "computeWithStringMethods"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"leetcode"}),
                    
                    new ProblemInfo("Longest Common Prefix",
                        new String[]{"LongestCommonPrefix", "Common Prefix", "LCP"},
                        "Write a function to find the longest common prefix string amongst an array of strings. If there is no common prefix, return an empty string.",
                        "Find longest common prefix among strings",
                        new String[]{"string", "array", "prefix", "common", "longest"},
                        "Strings",
                        "strings.LongestCommonPrefix",
                        new String[]{"computeHorizontal", "computeVertical", "computeWithStringBuilder"},
                        new String[]{"strs"},
                        new String[]{"String[]"},
                        new Object[]{new String[]{"flower","flow","flight"}}),
                    
                    new ProblemInfo("Longest Palindromic Substring",
                        new String[]{"LongestPalindromicSubstring", "Longest Palindrome", "Palindromic Substring"},
                        "Given a string s, return the longest palindromic substring in s.",
                        "Find longest palindromic substring",
                        new String[]{"string", "palindrome", "substring", "longest", "palindromic"},
                        "Strings",
                        "strings.LongestPalindromicSubstring",
                        new String[]{"computeExpandAroundCenters", "computeWithStringBuilder", "computeWithDP"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"babad"}),
                    
                    new ProblemInfo("Valid Palindrome II",
                        new String[]{"ValidPalindromeII", "Valid Palindrome Two"},
                        "Given a string s, return true if the s can be palindrome after deleting at most one character from it.",
                        "Check if string can be palindrome after deleting one character",
                        new String[]{"string", "palindrome", "delete", "character", "valid"},
                        "Strings",
                        "strings.ValidPalindromeII",
                        new String[]{"computeWithTwoPointers", "computeWithStringBuilder", "computeWithRecursion"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"aba"}),
                    
                    new ProblemInfo("Reverse String",
                        new String[]{"ReverseString", "Reverse"},
                        "Write a function that reverses a string. The input string is given as an array of characters s. You must do this by modifying the input array in-place with O(1) extra memory.",
                        "Reverse a string in-place",
                        new String[]{"string", "reverse", "array", "characters", "in-place"},
                        "Strings",
                        "strings.ReverseString",
                        new String[]{"computeWithTwoPointers", "computeWithStringBuilder", "computeWithRecursion"},
                        new String[]{"s"},
                        new String[]{"char[]"},
                        new Object[]{"hello".toCharArray()}),
                    
                    new ProblemInfo("Reverse Words in String",
                        new String[]{"ReverseWordsInString", "Reverse Words"},
                        "Given an input string s, reverse the order of the words. A word is defined as a sequence of non-space characters. The words in s will be separated by at least one space. Return a string of the words in reverse order concatenated by a single space.",
                        "Reverse the order of words in a string",
                        new String[]{"string", "words", "reverse", "order", "space"},
                        "Strings",
                        "strings.ReverseWordsInString",
                        new String[]{"computeWithBuiltIn", "computeWithStringBuilder", "computeWithTwoPass"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"the sky is blue"}),
                    
                    new ProblemInfo("Backspace String Compare",
                        new String[]{"BackspaceStringCompare", "Backspace Compare"},
                        "Given two strings s and t, return true if they are equal when both are typed into empty text editors. '#' means a backspace character.",
                        "Compare strings with backspace characters",
                        new String[]{"string", "backspace", "compare", "character", "#"},
                        "Strings",
                        "strings.BackspaceStringCompare",
                        new String[]{"computeWithStack", "computeWithStringBuilder", "computeWithTwoPointers"},
                        new String[]{"s", "t"},
                        new String[]{"String", "String"},
                        new Object[]{"ab#c", "ad#c"}),
                    
                    new ProblemInfo("Find All Anagrams in String",
                        new String[]{"FindAllAnagramsInString", "Find Anagrams"},
                        "Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.",
                        "Find all start indices of anagram substrings",
                        new String[]{"string", "anagram", "indices", "substring", "start"},
                        "Strings",
                        "strings.FindAllAnagramsInString",
                        new String[]{"computeWithHashMap", "computeWithArray", "computeWithStringBuilder"},
                        new String[]{"s", "p"},
                        new String[]{"String", "String"},
                        new Object[]{"cbaebabacd", "abc"}),
                    
                    new ProblemInfo("Decode String",
                        new String[]{"DecodeString", "Decode"},
                        "Given an encoded string, return its decoded string. The encoding rule is: k[encoded_string], where the encoded_string inside the square brackets is being repeated exactly k times.",
                        "Decode string with number-bracket pattern",
                        new String[]{"string", "decode", "brackets", "repeat", "number"},
                        "Strings",
                        "strings.DecodeString",
                        new String[]{"computeWithStack", "computeWithRecursion", "computeWithStringBuilder"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"3[a]2[bc]"}),
                    
                    new ProblemInfo("String to Integer (atoi)",
                        new String[]{"StringToInteger", "Atoi", "String to Integer"},
                        "Implement the myAtoi(string s) function, which converts a string to a 32-bit signed integer (similar to C/C++'s atoi function).",
                        "Convert string to 32-bit signed integer",
                        new String[]{"string", "integer", "conversion", "atoi", "parse", "number"},
                        "Strings",
                        "strings.StringToInteger",
                        new String[]{"computeIterative", "computeWithStringBuilder", "computeWithRecursion"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"42"}),
                    
                    new ProblemInfo("Implement strStr()",
                        new String[]{"ImplementStrStr", "StrStr", "Find Needle", "Needle in Haystack"},
                        "Given two strings needle and haystack, return the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.",
                        "Find first occurrence of needle in haystack",
                        new String[]{"string", "strstr", "needle", "haystack", "substring", "index", "kmp"},
                        "Strings",
                        "strings.ImplementStrStr",
                        new String[]{"computeWithBruteForce", "computeWithStringMethods", "computeWithStringBuilder", "computeWithKMP"},
                        new String[]{"haystack", "needle"},
                        new String[]{"String", "String"},
                        new Object[]{"sadbutsad", "sad"}),
                    
                    new ProblemInfo("Add Binary",
                        new String[]{"AddBinary", "Binary Add", "Binary Sum"},
                        "Given two binary strings a and b, return their sum as a binary string.",
                        "Add two binary strings",
                        new String[]{"string", "binary", "add", "sum", "carry", "stringbuilder"},
                        "Strings",
                        "strings.AddBinary",
                        new String[]{"computeWithStringBuilder", "computeWithBigInteger", "computeWithArray", "computeRecursive"},
                        new String[]{"a", "b"},
                        new String[]{"String", "String"},
                        new Object[]{"11", "1"})
                };
            
            case 3: // Trees
                return new ProblemInfo[] {
                    new ProblemInfo("Maximum Depth of Binary Tree",
                        new String[]{"MaximumDepthOfBinaryTree", "Max Depth", "Tree Depth"},
                        "Given the root of a binary tree, return its maximum depth. A binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.",
                        "Find the maximum depth of a binary tree",
                        new String[]{"tree", "binary tree", "depth", "maximum", "nodes", "root", "leaf", "path"},
                        "Trees",
                        "trees.MaximumDepthOfBinaryTree",
                        new String[]{"computeRecursive", "computeWithStack", "computeWithBFS"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Same Tree",
                        new String[]{"SameTree", "Identical Trees"},
                        "Given the roots of two binary trees p and q, return true if they are the same tree, and false otherwise. Two binary trees are considered the same if they are structurally identical, and the nodes have the same value.",
                        "Check if two binary trees are identical",
                        new String[]{"tree", "binary tree", "same", "identical", "structure", "value", "nodes"},
                        "Trees",
                        "trees.SameTree",
                        new String[]{"computeRecursive", "computeWithStack", "computeWithBFS"},
                        new String[]{"p", "q"},
                        new String[]{"TreeNode", "TreeNode"},
                        null),
                    
                    new ProblemInfo("Invert Binary Tree",
                        new String[]{"InvertBinaryTree", "Invert Tree", "Mirror Tree"},
                        "Given the root of a binary tree, invert the tree, and return its root.",
                        "Invert a binary tree",
                        new String[]{"tree", "binary tree", "invert", "mirror", "root"},
                        "Trees",
                        "trees.InvertBinaryTree",
                        new String[]{"computeRecursive", "computeWithStack", "computeWithQueue"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Symmetric Tree",
                        new String[]{"SymmetricTree", "Symmetric", "Mirror"},
                        "Given the root of a binary tree, check whether it is a mirror of itself (i.e., symmetric around its center).",
                        "Check if a binary tree is symmetric",
                        new String[]{"tree", "binary tree", "symmetric", "mirror", "center"},
                        "Trees",
                        "trees.SymmetricTree",
                        new String[]{"computeRecursive", "computeWithQueue", "computeWithStack"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Binary Tree Level Order Traversal",
                        new String[]{"BinaryTreeLevelOrderTraversal", "Level Order", "BFS"},
                        "Given the root of a binary tree, return the level order traversal of its nodes' values. (i.e., from left to right, level by level).",
                        "Return level order traversal of binary tree",
                        new String[]{"tree", "binary tree", "level order", "traversal", "bfs", "breadth first"},
                        "Trees",
                        "trees.BinaryTreeLevelOrderTraversal",
                        new String[]{"computeWithBFS", "computeWithDFS", "computeRecursive"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Validate Binary Search Tree",
                        new String[]{"ValidateBinarySearchTree", "Validate BST", "BST Validation"},
                        "Given the root of a binary tree, determine if it is a valid binary search tree (BST). A valid BST is defined as follows: The left subtree of a node contains only nodes with keys less than the node's key. The right subtree of a node contains only nodes with keys greater than the node's key. Both the left and right subtrees must also be binary search trees.",
                        "Check if binary tree is a valid BST",
                        new String[]{"tree", "binary tree", "bst", "binary search tree", "validate", "valid"},
                        "Trees",
                        "trees.ValidateBinarySearchTree",
                        new String[]{"computeRecursive", "computeWithInorder", "computeWithStack"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Binary Tree Inorder Traversal",
                        new String[]{"BinaryTreeInorderTraversal", "Inorder Traversal", "Inorder"},
                        "Given the root of a binary tree, return the inorder traversal of its nodes' values.",
                        "Return inorder traversal of binary tree",
                        new String[]{"tree", "binary tree", "inorder", "traversal", "dfs"},
                        "Trees",
                        "trees.BinaryTreeInorderTraversal",
                        new String[]{"computeRecursive", "computeWithStack", "computeMorris"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Binary Tree Maximum Path Sum",
                        new String[]{"BinaryTreeMaximumPathSum", "Max Path Sum", "Path Sum"},
                        "A path in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them. A node can only appear in the sequence at most once. Note that the path does not need to pass through the root. The path sum of a path is the sum of the node's values in the path. Given the root of a binary tree, return the maximum path sum of any non-empty path.",
                        "Find maximum path sum in binary tree",
                        new String[]{"tree", "binary tree", "path", "sum", "maximum", "dfs"},
                        "Trees",
                        "trees.BinaryTreeMaximumPathSum",
                        new String[]{"computeRecursive", "computeWithArray"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Subtree of Another Tree",
                        new String[]{"SubtreeOfAnotherTree", "Subtree", "Contains Subtree"},
                        "Given the roots of two binary trees root and subRoot, return true if there is a subtree of root with the same structure and node values of subRoot and false otherwise. A subtree of a binary tree tree is a tree that consists of a node in tree and all of this node's descendants. The tree tree could also be considered as a subtree of itself.",
                        "Check if subRoot is a subtree of root",
                        new String[]{"tree", "binary tree", "subtree", "contains", "same structure"},
                        "Trees",
                        "trees.SubtreeOfAnotherTree",
                        new String[]{"computeRecursive", "computeWithSerialization"},
                        new String[]{"root", "subRoot"},
                        new String[]{"TreeNode", "TreeNode"},
                        null),
                    
                    new ProblemInfo("Lowest Common Ancestor of Binary Tree",
                        new String[]{"LowestCommonAncestorOfBinaryTree", "LCA", "Lowest Common Ancestor"},
                        "Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree. According to the definition of LCA on Wikipedia: 'The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a descendant of itself).'",
                        "Find lowest common ancestor of two nodes",
                        new String[]{"tree", "binary tree", "lca", "lowest common ancestor", "nodes"},
                        "Trees",
                        "trees.LowestCommonAncestorOfBinaryTree",
                        new String[]{"computeRecursive", "computeWithPath"},
                        new String[]{"root", "p", "q"},
                        new String[]{"TreeNode", "TreeNode", "TreeNode"},
                        null),
                    
                    new ProblemInfo("Binary Tree Right Side View",
                        new String[]{"BinaryTreeRightSideView", "Right Side View", "Right View"},
                        "Given the root of a binary tree, imagine yourself standing on the right side of it, return the values of the nodes you can see ordered from top to bottom.",
                        "Return right side view of binary tree",
                        new String[]{"tree", "binary tree", "right side", "view", "bfs", "level order"},
                        "Trees",
                        "trees.BinaryTreeRightSideView",
                        new String[]{"computeWithBFS", "computeWithDFS", "computeRecursive"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null),
                    
                    new ProblemInfo("Path Sum",
                        new String[]{"PathSum", "Tree Path Sum", "Sum Path"},
                        "Given the root of a binary tree and an integer targetSum, return true if the tree has a root-to-leaf path such that adding up all the values along the path equals targetSum. A leaf is a node with no children.",
                        "Check if tree has path with given sum",
                        new String[]{"tree", "binary tree", "path", "sum", "target", "root to leaf", "dfs"},
                        "Trees",
                        "trees.PathSum",
                        new String[]{"computeRecursive", "computeWithStack", "computeWithBFS"},
                        new String[]{"root", "targetSum"},
                        new String[]{"TreeNode", "int"},
                        null),
                    
                    new ProblemInfo("Construct Binary Tree from Preorder and Inorder",
                        new String[]{"ConstructBinaryTreeFromPreorderAndInorder", "Build Tree", "Construct Tree"},
                        "Given two integer arrays preorder and inorder where preorder is the preorder traversal of a binary tree and inorder is the inorder traversal of the same tree, construct and return the binary tree.",
                        "Construct binary tree from preorder and inorder",
                        new String[]{"tree", "binary tree", "construct", "build", "preorder", "inorder", "traversal"},
                        "Trees",
                        "trees.ConstructBinaryTreeFromPreorderAndInorder",
                        new String[]{"computeRecursive", "computeRecursiveLinear"},
                        new String[]{"preorder", "inorder"},
                        new String[]{"int[]", "int[]"},
                        new Object[]{new int[]{3,9,20,15,7}, new int[]{9,3,15,20,7}}),
                    
                    new ProblemInfo("Binary Tree Preorder Traversal",
                        new String[]{"BinaryTreePreorderTraversal", "Preorder Traversal", "Preorder"},
                        "Given the root of a binary tree, return the preorder traversal of its nodes' values.",
                        "Return preorder traversal of binary tree",
                        new String[]{"tree", "binary tree", "preorder", "traversal", "dfs"},
                        "Trees",
                        "trees.BinaryTreePreorderTraversal",
                        new String[]{"computeRecursive", "computeWithStack", "computeMorris"},
                        new String[]{"root"},
                        new String[]{"TreeNode"},
                        null)
                };
            
            case 4: // Linked List
                return new ProblemInfo[] {
                    new ProblemInfo("Reverse Linked List",
                        new String[]{"ReverseLinkedList", "Reverse List"},
                        "Given the head of a singly linked list, reverse the list, and return the reversed list.",
                        "Reverse a singly linked list",
                        new String[]{"linked list", "reverse", "singly", "head", "nodes"},
                        "Linked List",
                        "linkedlist.ReverseLinkedList",
                        new String[]{"computeIterative", "computeRecursive", "computeWithStack"},
                        new String[]{"head"},
                        new String[]{"ListNode"},
                        null),
                    
                    new ProblemInfo("Merge Two Sorted Lists",
                        new String[]{"MergeTwoSortedLists", "Merge Lists"},
                        "You are given the heads of two sorted linked lists list1 and list2. Merge the two lists in a one sorted list. The list should be made by splicing together the nodes of the first two lists. Return the head of the merged linked list.",
                        "Merge two sorted linked lists",
                        new String[]{"linked list", "merge", "sorted", "heads", "nodes"},
                        "Linked List",
                        "linkedlist.MergeTwoSortedLists",
                        new String[]{"computeIterative", "computeRecursive"},
                        new String[]{"list1", "list2"},
                        new String[]{"ListNode", "ListNode"},
                        null),
                    
                    new ProblemInfo("Linked List Cycle",
                        new String[]{"LinkedListCycle", "Cycle Detection"},
                        "Given head, the head of a linked list, determine if the linked list has a cycle in it. There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the next pointer.",
                        "Detect if a linked list has a cycle",
                        new String[]{"linked list", "cycle", "head", "node", "pointer", "detect"},
                        "Linked List",
                        "linkedlist.LinkedListCycle",
                        new String[]{"computeWithHashSet", "computeWithTwoPointers"},
                        new String[]{"head"},
                        new String[]{"ListNode"},
                        null),
                    
                    new ProblemInfo("Add Two Numbers",
                        new String[]{"AddTwoNumbers", "Sum Lists"},
                        "You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.",
                        "Add two numbers represented by linked lists",
                        new String[]{"linked list", "add", "numbers", "sum", "digits", "reverse order"},
                        "Linked List",
                        "linkedlist.AddTwoNumbers",
                        new String[]{"computeIterative", "computeRecursive"},
                        new String[]{"l1", "l2"},
                        new String[]{"ListNode", "ListNode"},
                        null),
                    
                    new ProblemInfo("Remove Nth Node From End of List",
                        new String[]{"RemoveNthNodeFromEnd", "Remove Nth Node", "Remove Node"},
                        "Given the head of a linked list, remove the nth node from the end of the list and return its head.",
                        "Remove the nth node from end of linked list",
                        new String[]{"linked list", "remove", "nth", "node", "end", "head"},
                        "Linked List",
                        "linkedlist.RemoveNthNodeFromEnd",
                        new String[]{"computeWithTwoPointers", "computeWithTwoPass", "computeRecursive"},
                        new String[]{"head", "n"},
                        new String[]{"ListNode", "int"},
                        null),
                    
                    new ProblemInfo("Palindrome Linked List",
                        new String[]{"PalindromeLinkedList", "Palindrome List", "Linked List Palindrome"},
                        "Given the head of a singly linked list, return true if it is a palindrome or false otherwise.",
                        "Check if linked list is palindrome",
                        new String[]{"linked list", "palindrome", "singly", "reverse", "compare"},
                        "Linked List",
                        "linkedlist.PalindromeLinkedList",
                        new String[]{"computeWithReversal", "computeWithStack", "computeRecursive"},
                        new String[]{"head"},
                        new String[]{"ListNode"},
                        null),
                    
                    new ProblemInfo("Linked List Cycle II",
                        new String[]{"LinkedListCycleII", "Cycle II", "Find Cycle Start"},
                        "Given the head of a linked list, return the node where the cycle begins. If there is no cycle, return null. There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the next pointer.",
                        "Find the node where cycle begins in linked list",
                        new String[]{"linked list", "cycle", "node", "begin", "start", "floyd"},
                        "Linked List",
                        "linkedlist.LinkedListCycleII",
                        new String[]{"computeWithTwoPointers", "computeWithHashSet"},
                        new String[]{"head"},
                        new String[]{"ListNode"},
                        null),
                    
                    new ProblemInfo("Reorder List",
                        new String[]{"ReorderList", "Reorder", "L0 Ln L1"},
                        "You are given the head of a singly linked-list. The list can be represented as: L0 → L1 → … → Ln - 1 → Ln. Reorder the list to be on the following form: L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …. You may not modify the values in the list's nodes. Only nodes themselves may be changed.",
                        "Reorder linked list: L0→Ln→L1→Ln-1...",
                        new String[]{"linked list", "reorder", "middle", "reverse", "merge"},
                        "Linked List",
                        "linkedlist.ReorderList",
                        new String[]{"compute", "computeWithStack"},
                        new String[]{"head"},
                        new String[]{"ListNode"},
                        null),
                    
                    new ProblemInfo("Merge k Sorted Lists",
                        new String[]{"MergeKSortedLists", "Merge K Lists", "K Sorted Lists"},
                        "You are given an array of k linked-lists lists, each linked-list is sorted in ascending order. Merge all the linked-lists into one sorted linked-list and return it.",
                        "Merge k sorted linked lists into one",
                        new String[]{"linked list", "merge", "k", "sorted", "heap", "priority queue", "divide conquer"},
                        "Linked List",
                        "linkedlist.MergeKSortedLists",
                        new String[]{"computeWithHeap", "computeWithDivideConquer", "computeIterative"},
                        new String[]{"lists"},
                        new String[]{"ListNode[]"},
                        null),
                    
                    new ProblemInfo("Remove Duplicates from Sorted List",
                        new String[]{"RemoveDuplicatesFromSortedList", "Remove Duplicates List", "Unique List"},
                        "Given the head of a sorted linked list, delete all duplicates such that each element appears only once. Return the linked list sorted as well.",
                        "Remove duplicates from sorted linked list",
                        new String[]{"linked list", "remove", "duplicates", "sorted", "unique"},
                        "Linked List",
                        "linkedlist.RemoveDuplicatesFromSortedList",
                        new String[]{"computeIterative", "computeRecursive", "computeWithHashSet"},
                        new String[]{"head"},
                        new String[]{"ListNode"},
                        null)
                };
            
            case 5: // Two Pointers
                return new ProblemInfo[] {
                    new ProblemInfo("Valid Palindrome",
                        new String[]{"ValidPalindrome", "Palindrome"},
                        "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.",
                        "Check if string is palindrome",
                        new String[]{"string", "palindrome", "alphanumeric", "forward", "backward"},
                        "Two Pointers",
                        "pointers.ValidPalindrome",
                        new String[]{"computeWithTwoPointers", "computeWithStringBuilder", "computeWithRecursion"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"A man a plan a canal Panama"}),
                    
                    new ProblemInfo("Two Sum II",
                        new String[]{"TwoSumII", "Two Sum Two", "Sorted Two Sum"},
                        "Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number. Return the indices of the two numbers (1-indexed).",
                        "Find two numbers that add to target in sorted array",
                        new String[]{"array", "sorted", "target", "two", "sum", "indices", "increasing"},
                        "Two Pointers",
                        "pointers.TwoSumII",
                        new String[]{"computeWithTwoPointers", "computeWithBinarySearch", "computeWithHashMap"},
                        new String[]{"numbers", "target"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{2,7,11,15}, 9}),
                    
                    new ProblemInfo("Container With Most Water",
                        new String[]{"ContainerWithMostWater", "Most Water", "Container"},
                        "You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]). Find two lines that together with the x-axis form a container, such that the container contains the most water.",
                        "Find container that holds most water",
                        new String[]{"array", "height", "container", "water", "area", "two pointers"},
                        "Two Pointers",
                        "pointers.ContainerWithMostWater",
                        new String[]{"computeWithTwoPointers", "computeWithBruteForce", "computeWithRecursion"},
                        new String[]{"height"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,8,6,2,5,4,8,3,7}}),
                    
                    new ProblemInfo("3Sum",
                        new String[]{"ThreeSum", "3 Sum", "Three Sum"},
                        "Given an integer array nums, return all the triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0. The solution set must not contain duplicate triplets.",
                        "Find all triplets that sum to zero",
                        new String[]{"array", "three", "sum", "triplets", "zero", "duplicate"},
                        "Two Pointers",
                        "pointers.ThreeSum",
                        new String[]{"computeWithTwoPointers", "computeWithHashSet", "computeWithBruteForce"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{-1,0,1,2,-1,-4}}),
                    
                    new ProblemInfo("Trapping Rain Water",
                        new String[]{"TrappingRainWater", "Rain Water", "Trapping Water"},
                        "Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.",
                        "Calculate trapped rainwater",
                        new String[]{"array", "height", "water", "trapping", "rain", "elevation"},
                        "Two Pointers",
                        "pointers.TrappingRainWater",
                        new String[]{"computeWithTwoPointers", "computeWithDP", "computeWithBruteForce"},
                        new String[]{"height"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{0,1,0,2,1,0,1,3,2,1,2,1}}),
                    
                    new ProblemInfo("Remove Duplicates from Sorted Array",
                        new String[]{"RemoveDuplicatesFromSortedArray", "Remove Duplicates", "Unique Elements"},
                        "Given an integer array nums sorted in non-decreasing order, remove the duplicates in-place such that each unique element appears only once. The relative order of the elements should be kept the same. Then return the number of unique elements in nums.",
                        "Remove duplicates from sorted array in-place",
                        new String[]{"array", "duplicates", "sorted", "unique", "in-place", "two pointers"},
                        "Two Pointers",
                        "pointers.RemoveDuplicatesFromSortedArray",
                        new String[]{"computeWithTwoPointers", "computeWithArrayList", "computeWithBruteForce"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,1,2}}),
                    
                    new ProblemInfo("Move Zeroes",
                        new String[]{"MoveZeroes", "Move Zero", "Zeros to End"},
                        "Given an integer array nums, move all 0's to the end of it while maintaining the relative order of the non-zero elements. Note that you must do this in-place without making a copy of the array.",
                        "Move all zeros to end maintaining order",
                        new String[]{"array", "zeroes", "zeros", "move", "in-place", "two pointers"},
                        "Two Pointers",
                        "pointers.MoveZeroes",
                        new String[]{"computeWithTwoPointers", "computeWithSwapping", "computeWithArrayList"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{0,1,0,3,12}}),
                    
                    new ProblemInfo("Sort Colors",
                        new String[]{"SortColors", "Dutch National Flag", "Three Colors"},
                        "Given an array nums with n objects colored red, white, or blue, sort them in-place so that objects of the same color are adjacent, with the colors in the order red, white, and blue. We will use the integers 0, 1, and 2 to represent the color red, white, and blue, respectively. You must solve this problem without using the library's sort function.",
                        "Sort array of 0s, 1s, and 2s (Dutch Flag)",
                        new String[]{"array", "sort", "colors", "three", "dutch flag", "0 1 2", "in-place"},
                        "Two Pointers",
                        "pointers.SortColors",
                        new String[]{"computeWithThreePointers", "computeWithCounting", "computeWithTwoPass"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{2,0,2,1,1,0}}),
                    
                    new ProblemInfo("4Sum",
                        new String[]{"FourSum", "4Sum", "Four Sum"},
                        "Given an array nums of n integers, return an array of all the unique quadruplets [nums[a], nums[b], nums[c], nums[d]] such that: 0 <= a, b, c, d < n, a, b, c, and d are distinct, nums[a] + nums[b] + nums[c] + nums[d] == target. You may return the answer in any order.",
                        "Find all unique quadruplets that sum to target",
                        new String[]{"array", "four", "sum", "quadruplets", "target", "two pointers", "unique"},
                        "Two Pointers",
                        "pointers.FourSum",
                        new String[]{"computeWithTwoPointers", "computeWithHashSet", "computeWithBruteForce"},
                        new String[]{"nums", "target"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{1,0,-1,0,-2,2}, 0})
                };
            
            case 6: // Sliding Window
                return new ProblemInfo[] {
                    new ProblemInfo("Best Time to Buy and Sell Stock",
                        new String[]{"BestTimeToBuySellStock", "Buy Sell Stock", "Stock Price"},
                        "You are given an array prices where prices[i] is the price of a given stock on the ith day. You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock. Return the maximum profit you can achieve from this transaction.",
                        "Find maximum profit from buying and selling stock",
                        new String[]{"array", "prices", "stock", "profit", "buy", "sell", "maximum"},
                        "Sliding Window",
                        "window.BestTimeToBuySellStock",
                        new String[]{"computeWithOnePass", "computeWithTwoPointers", "computeWithBruteForce"},
                        new String[]{"prices"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{7,1,5,3,6,4}}),
                    
                    new ProblemInfo("Longest Substring Without Repeating",
                        new String[]{"LongestSubstringWithoutRepeating", "Longest Substring", "No Repeating"},
                        "Given a string s, find the length of the longest substring without repeating characters.",
                        "Find longest substring without repeating characters",
                        new String[]{"string", "substring", "longest", "repeating", "characters", "unique"},
                        "Sliding Window",
                        "window.LongestSubstringWithoutRepeating",
                        new String[]{"computeWithHashSet", "computeWithHashMap", "computeWithStringBuilder", "computeWithArray"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"abcabcbb"}),
                    
                    new ProblemInfo("Longest Repeating Character Replacement",
                        new String[]{"LongestRepeatingCharacterReplacement", "Character Replacement", "Repeating Replacement"},
                        "You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English letter. You can perform this operation at most k times. Return the length of the longest substring containing the same letter you can get after performing the above operations.",
                        "Find longest substring after replacing k characters",
                        new String[]{"string", "substring", "replace", "character", "longest", "k"},
                        "Sliding Window",
                        "window.LongestRepeatingCharacterReplacement",
                        new String[]{"computeWithHashMap", "computeWithArray", "computeWithStringBuilder"},
                        new String[]{"s", "k"},
                        new String[]{"String", "int"},
                        new Object[]{"AABABBA", 1}),
                    
                    new ProblemInfo("Minimum Window Substring",
                        new String[]{"MinimumWindowSubstring", "Min Window", "Window Substring"},
                        "Given two strings s and t, return the minimum window substring of s such that every character in t (including duplicates) is included in the window. If there is no such substring, return the empty string \"\".",
                        "Find minimum window substring containing all characters of t",
                        new String[]{"string", "substring", "window", "minimum", "sliding window", "contains"},
                        "Sliding Window",
                        "window.MinimumWindowSubstring",
                        new String[]{"computeWithHashMap", "computeWithArray"},
                        new String[]{"s", "t"},
                        new String[]{"String", "String"},
                        new Object[]{"ADOBECODEBANC", "ABC"}),
                    
                    new ProblemInfo("Minimum Size Subarray Sum",
                        new String[]{"MinimumSizeSubarraySum", "Min Size Subarray", "Subarray Sum"},
                        "Given an array of positive integers nums and a positive integer target, return the minimal length of a contiguous subarray [numsl, numsl+1, ..., numsr-1, numsr] of which the sum is greater than or equal to target. If there is no such subarray, return 0 instead.",
                        "Find minimum length subarray with sum >= target",
                        new String[]{"array", "subarray", "sum", "target", "minimum", "length", "sliding window"},
                        "Sliding Window",
                        "window.MinimumSizeSubarraySum",
                        new String[]{"computeWithSlidingWindow", "computeWithBinarySearch", "computeWithBruteForce"},
                        new String[]{"target", "nums"},
                        new String[]{"int", "int[]"},
                        new Object[]{7, new int[]{2,3,1,2,4,3}}),
                    
                    new ProblemInfo("Permutation in String",
                        new String[]{"PermutationInString", "Permutation String", "s2 Contains s1"},
                        "Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise. In other words, return true if one of s1's permutations is the substring of s2.",
                        "Check if s2 contains permutation of s1",
                        new String[]{"string", "permutation", "substring", "contains", "sliding window", "anagram"},
                        "Sliding Window",
                        "window.PermutationInString",
                        new String[]{"computeWithArray", "computeWithHashMap", "computeWithBruteForce"},
                        new String[]{"s1", "s2"},
                        new String[]{"String", "String"},
                        new Object[]{"ab", "eidbaooo"}),
                    
                    new ProblemInfo("Substring with Concatenation of All Words",
                        new String[]{"SubstringWithConcatenationOfAllWords", "Concatenation", "All Words"},
                        "You are given a string s and an array of strings words. All the strings of words are of the same length. A concatenated substring in s is a substring that contains all the strings of words concatenated in any order. Return the starting indices of all such concatenated substrings in s.",
                        "Find indices of substrings containing all words",
                        new String[]{"string", "substring", "concatenation", "words", "indices", "sliding window", "hashmap"},
                        "Sliding Window",
                        "window.SubstringWithConcatenationOfAllWords",
                        new String[]{"computeWithHashMap", "computeWithSlidingWindow"},
                        new String[]{"s", "words"},
                        new String[]{"String", "String[]"},
                        new Object[]{"barfoothefoobarman", new String[]{"foo","bar"}})
                };
            
            case 7: // Stack
                return new ProblemInfo[] {
                    new ProblemInfo("Valid Parentheses",
                        new String[]{"ValidParentheses", "Parentheses", "Valid Brackets"},
                        "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid. An input string is valid if: 1) Open brackets must be closed by the same type of brackets. 2) Open brackets must be closed in the correct order. 3) Every close bracket has a corresponding open bracket of the same type.",
                        "Check if parentheses/brackets are valid",
                        new String[]{"string", "parentheses", "brackets", "valid", "stack", "matching"},
                        "Stack",
                        "stack.ValidParentheses",
                        new String[]{"computeWithStack", "computeWithHashMap", "computeWithStringBuilder"},
                        new String[]{"s"},
                        new String[]{"String"},
                        new Object[]{"()[]{}"}),
                    
                    new ProblemInfo("Daily Temperatures",
                        new String[]{"DailyTemperatures", "Temperatures", "Next Warmer"},
                        "Given an array of integers temperatures represents the daily temperatures, return an array answer such that answer[i] is the number of days you have to wait after the ith day to get a warmer temperature. If there is no future day for which this is possible, keep answer[i] == 0 instead.",
                        "Find days to wait for warmer temperature",
                        new String[]{"array", "temperatures", "warmer", "days", "wait", "next"},
                        "Stack",
                        "stack.DailyTemperatures",
                        new String[]{"computeWithStack", "computeWithArray", "computeWithBruteForce"},
                        new String[]{"temperatures"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{73,74,75,71,69,72,76,73}}),
                    
                    new ProblemInfo("Largest Rectangle in Histogram",
                        new String[]{"LargestRectangleInHistogram", "Largest Rectangle", "Histogram"},
                        "Given an array of integers heights representing the histogram's bar height where the width of each bar is 1, return the area of the largest rectangle in the histogram.",
                        "Find largest rectangle area in histogram",
                        new String[]{"array", "heights", "histogram", "rectangle", "largest", "area", "monotonic stack"},
                        "Stack",
                        "stack.LargestRectangleInHistogram",
                        new String[]{"computeWithStack", "computeWithBruteForce", "computeWithDivideConquer"},
                        new String[]{"heights"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{2,1,5,6,2,3}}),
                    
                    new ProblemInfo("Evaluate Reverse Polish Notation",
                        new String[]{"EvaluateReversePolishNotation", "RPN", "Polish Notation"},
                        "You are given an array of strings tokens that represents an arithmetic expression in a Reverse Polish Notation. Evaluate the expression and return an integer that represents the value of the expression. Note that: The valid operators are '+', '-', '*', and '/'. Each operand may be an integer or another expression. The division between two integers always truncates toward zero. There will not be any division by zero.",
                        "Evaluate RPN expression",
                        new String[]{"array", "tokens", "rpn", "reverse polish notation", "expression", "stack", "evaluate"},
                        "Stack",
                        "stack.EvaluateReversePolishNotation",
                        new String[]{"computeWithStack", "computeWithArray", "computeRecursive"},
                        new String[]{"tokens"},
                        new String[]{"String[]"},
                        new Object[]{new String[]{"2","1","+","3","*"}}),
                    
                    new ProblemInfo("Generate Parentheses",
                        new String[]{"GenerateParentheses", "Generate Brackets", "Valid Parentheses Combinations"},
                        "Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.",
                        "Generate all valid parentheses combinations",
                        new String[]{"parentheses", "brackets", "generate", "combinations", "well-formed", "backtracking", "recursive"},
                        "Stack",
                        "stack.GenerateParentheses",
                        new String[]{"computeRecursive", "computeWithRecursion", "computeWithStack"},
                        new String[]{"n"},
                        new String[]{"int"},
                        new Object[]{3}),
                    
                    new ProblemInfo("Car Fleet",
                        new String[]{"CarFleet", "Fleet", "Cars"},
                        "There are n cars going to the same destination on a one-lane road. The destination is target miles away. You are given two integer array position and speed, both of length n, where position[i] is the position of the ith car and speed[i] is the speed of the ith car (in miles per hour). A car can never pass another car ahead of it, but it can catch up to it and drive bumper to bumper at the same speed. Return the number of car fleets that will arrive at the destination.",
                        "Count number of car fleets reaching destination",
                        new String[]{"cars", "fleet", "position", "speed", "target", "stack", "monotonic"},
                        "Stack",
                        "stack.CarFleet",
                        new String[]{"computeWithStack", "computeWithArray", "computeWithTreeMap"},
                        new String[]{"target", "position", "speed"},
                        new String[]{"int", "int[]", "int[]"},
                        new Object[]{12, new int[]{10,8,0,5,3}, new int[]{2,4,1,1,3}})
                };
            
            case 8: // Search
                return new ProblemInfo[] {
                    new ProblemInfo("Search in Rotated Sorted Array",
                        new String[]{"SearchInRotatedSortedArray", "Rotated Array", "Search Rotated"},
                        "There is an integer array nums sorted in ascending order (with distinct values). Prior to being passed to your function, nums is possibly rotated at an unknown pivot index k such that the resulting array is [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]. Given the array nums after the rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.",
                        "Search target in rotated sorted array",
                        new String[]{"array", "rotated", "sorted", "search", "target", "pivot", "binary search"},
                        "Search",
                        "search.SearchInRotatedSortedArray",
                        new String[]{"computeWithBinarySearch", "computeWithPivotSearch", "computeWithLinearSearch"},
                        new String[]{"nums", "target"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{4,5,6,7,0,1,2}, 0}),
                    
                    new ProblemInfo("Find Minimum in Rotated Sorted Array",
                        new String[]{"FindMinimumInRotatedSortedArray", "Min Rotated Array", "Minimum Rotated"},
                        "Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array nums = [0,1,2,4,5,6,7] might become [4,5,6,7,0,1,2] if it was rotated 4 times. Given the sorted rotated array nums of unique elements, return the minimum element of this array.",
                        "Find minimum element in rotated sorted array",
                        new String[]{"array", "rotated", "sorted", "minimum", "element", "binary search"},
                        "Search",
                        "search.FindMinimumInRotatedSortedArray",
                        new String[]{"computeWithBinarySearch", "computeWithLinearSearch", "computeWithRecursion"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{3,4,5,1,2}}),
                    
                    new ProblemInfo("Find Peak Element",
                        new String[]{"FindPeakElement", "Peak Element", "Peak"},
                        "A peak element is an element that is strictly greater than its neighbors. Given a 0-indexed integer array nums, find a peak element, and return its index. If the array contains multiple peaks, return the index to any of the peaks. You may imagine that nums[-1] = nums[n] = -∞. You must write an algorithm that runs in O(log n) time.",
                        "Find peak element in array",
                        new String[]{"array", "peak", "element", "neighbors", "binary search", "log n"},
                        "Search",
                        "search.FindPeakElement",
                        new String[]{"computeWithBinarySearch", "computeWithLinearSearch", "computeRecursive"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,2,3,1}}),
                    
                    new ProblemInfo("Search a 2D Matrix",
                        new String[]{"SearchA2DMatrix", "2D Matrix Search", "Matrix Search"},
                        "You are given an m x n integer matrix matrix with the following properties: Each row is sorted in non-decreasing order. The first integer of each row is greater than the last integer of the previous row. Given an integer target, return true if target is in matrix or false otherwise.",
                        "Search target in sorted 2D matrix",
                        new String[]{"matrix", "2d", "sorted", "target", "binary search", "rows"},
                        "Search",
                        "search.SearchA2DMatrix",
                        new String[]{"computeWithBinarySearch", "computeWithTwoBinarySearches", "computeWithLinearSearch"},
                        new String[]{"matrix", "target"},
                        new String[]{"int[][]", "int"},
                        new Object[]{new int[][]{{1,3,5,7},{10,11,16,20},{23,30,34,60}}, 3}),
                    
                    new ProblemInfo("Binary Search",
                        new String[]{"BinarySearch", "Search", "Binary"},
                        "Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, then return its index. Otherwise, return -1. You must write an algorithm with O(log n) runtime complexity.",
                        "Search target in sorted array using binary search",
                        new String[]{"array", "sorted", "target", "binary search", "log n", "search"},
                        "Search",
                        "search.BinarySearch",
                        new String[]{"computeIterative", "computeRecursive", "computeWithArrays"},
                        new String[]{"nums", "target"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{-1,0,3,5,9,12}, 9}),
                    
                    new ProblemInfo("First Bad Version",
                        new String[]{"FirstBadVersion", "Bad Version", "Version"},
                        "You are a product manager and currently leading a team to develop a new product. Unfortunately, the latest version of your product fails the quality check. Since each version is developed based on the previous version, all the versions after a bad version are also bad. Suppose you have n versions [1, 2, ..., n] and you want to find out the first bad one, which causes all the following ones to be bad. You are given an API bool isBadVersion(version) which returns whether version is bad. Implement a function to find the first bad version. You should minimize the number of calls to the API.",
                        "Find first bad version using binary search",
                        new String[]{"version", "bad", "binary search", "log n", "api", "search"},
                        "Search",
                        "search.FirstBadVersion",
                        new String[]{"computeWithBinarySearch", "computeRecursive", "computeWithLinearSearch"},
                        new String[]{"n"},
                        new String[]{"int"},
                        new Object[]{5}),
                    
                    new ProblemInfo("Sqrt(x)",
                        new String[]{"Sqrt", "Square Root", "Root"},
                        "Given a non-negative integer x, return the square root of x rounded down to the nearest integer. The returned integer should be non-negative as well. You must not use any built-in exponent function or operator.",
                        "Find square root of x (rounded down)",
                        new String[]{"math", "sqrt", "square root", "binary search", "newton"},
                        "Search",
                        "search.Sqrt",
                        new String[]{"computeWithBinarySearch", "computeWithNewton", "computeWithMath", "computeWithLinearSearch"},
                        new String[]{"x"},
                        new String[]{"int"},
                        new Object[]{8})
                };
            
            case 9: // Dynamic Programming
                return new ProblemInfo[] {
                    new ProblemInfo("Climbing Stairs",
                        new String[]{"ClimbingStairs", "Stairs", "Fibonacci"},
                        "You are climbing a staircase. It takes n steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?",
                        "Find distinct ways to climb n steps (1 or 2 at a time)",
                        new String[]{"stairs", "steps", "distinct", "ways", "climbing", "fibonacci", "dp"},
                        "Dynamic Programming",
                        "dp.ClimbingStairs",
                        new String[]{"computeWithDP", "computeWithOptimizedSpace", "computeWithRecursion", "computeWithPureRecursion"},
                        new String[]{"n"},
                        new String[]{"int"},
                        new Object[]{5}),
                    
                    new ProblemInfo("House Robber",
                        new String[]{"HouseRobber", "Robber", "Max Sum No Adjacent"},
                        "You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and it will automatically contact the police if two adjacent houses were broken into on the same night. Given an integer array nums representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.",
                        "Find maximum money robbing houses (no two adjacent)",
                        new String[]{"houses", "robber", "money", "maximum", "adjacent", "dp", "constraint"},
                        "Dynamic Programming",
                        "dp.HouseRobber",
                        new String[]{"computeWithDP", "computeWithOptimizedSpace", "computeWithRecursion", "computeWithPureRecursion"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{2,7,9,3,1}}),
                    
                    new ProblemInfo("Coin Change",
                        new String[]{"CoinChange", "Min Coins", "Change"},
                        "You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money. Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1. You may assume that you have an infinite number of each kind of coin.",
                        "Find minimum coins to make amount",
                        new String[]{"coins", "amount", "minimum", "fewest", "change", "dp", "unbounded"},
                        "Dynamic Programming",
                        "dp.CoinChange",
                        new String[]{"computeWithDP", "computeWithRecursion", "computeWithPureRecursion"},
                        new String[]{"coins", "amount"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{1,2,5}, 11}),
                    
                    new ProblemInfo("Unique Paths",
                        new String[]{"UniquePaths", "Robot Paths", "Grid Paths"},
                        "There is a robot on an m x n grid. The robot is initially located at the top-left corner (grid[0][0]). The robot tries to move to the bottom-right corner (grid[m - 1][n - 1]). The robot can only move either down or right at any point in time. Given the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.",
                        "Count unique paths from top-left to bottom-right",
                        new String[]{"grid", "robot", "paths", "unique", "m", "n", "dp", "2d"},
                        "Dynamic Programming",
                        "dp.UniquePaths",
                        new String[]{"computeWithDP", "computeWithOptimizedSpace", "computeWithRecursion"},
                        new String[]{"m", "n"},
                        new String[]{"int", "int"},
                        new Object[]{3, 7}),
                    
                    new ProblemInfo("Word Break",
                        new String[]{"WordBreak", "Break", "Segment"},
                        "Given a string s and a dictionary of strings wordDict, return true if s can be segmented into a space-separated sequence of one or more dictionary words. Note that the same word in the dictionary may be reused multiple times in the segmentation.",
                        "Check if string can be segmented into dictionary words",
                        new String[]{"string", "word", "break", "segment", "dictionary", "dp", "recursion"},
                        "Dynamic Programming",
                        "dp.WordBreak",
                        new String[]{"computeWithDP", "computeWithRecursion", "computeWithBFS"},
                        new String[]{"s", "wordDict"},
                        new String[]{"String", "List<String>"},
                        new Object[]{"leetcode", java.util.Arrays.asList("leet","code")}),
                    
                    new ProblemInfo("Longest Increasing Subsequence",
                        new String[]{"LongestIncreasingSubsequence", "LIS", "Increasing Subsequence"},
                        "Given an integer array nums, return the length of the longest strictly increasing subsequence.",
                        "Find length of longest increasing subsequence",
                        new String[]{"array", "subsequence", "increasing", "longest", "dp", "binary search"},
                        "Dynamic Programming",
                        "dp.LongestIncreasingSubsequence",
                        new String[]{"computeWithDP", "computeWithBinarySearch", "computeRecursive"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{10,9,2,5,3,7,101,18}})
                };
            
            case 10: // Graphs
                return new ProblemInfo[] {
                    new ProblemInfo("Number of Islands",
                        new String[]{"NumberOfIslands", "Islands", "Grid Islands"},
                        "Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.",
                        "Count number of islands in 2D grid",
                        new String[]{"grid", "2d", "islands", "land", "water", "dfs", "bfs", "graph"},
                        "Graphs",
                        "graph.NumberOfIslands",
                        new String[]{"computeWithDFS", "computeWithBFS", "computeWithUnionFind"},
                        new String[]{"grid"},
                        new String[]{"char[][]"},
                        new Object[]{new char[][]{{'1','1','1','1','0'},{'1','1','0','1','0'},{'1','1','0','0','0'},{'0','0','0','0','0'}}})
                };
            
            case 11: // Heap/Priority Queue
                return new ProblemInfo[] {
                    new ProblemInfo("Kth Largest Element in Array",
                        new String[]{"KthLargestElementInArray", "Kth Largest", "K Largest"},
                        "Given an integer array nums and an integer k, return the kth largest element in the array. Note that it is the kth largest element in the sorted order, not the kth distinct element. Can you solve it without sorting?",
                        "Find kth largest element in array",
                        new String[]{"array", "kth", "largest", "element", "heap", "priority queue", "quickselect"},
                        "Heap/Priority Queue",
                        "heap.KthLargestElementInArray",
                        new String[]{"computeWithHeap", "computeWithMaxHeap", "computeWithQuickSelect", "computeWithSorting"},
                        new String[]{"nums", "k"},
                        new String[]{"int[]", "int"},
                        new Object[]{new int[]{3,2,1,5,6,4}, 2})
                };
            
            case 12: // Backtracking
                return new ProblemInfo[] {
                    new ProblemInfo("Subsets",
                        new String[]{"Subsets", "Power Set", "All Subsets"},
                        "Given an integer array nums of unique elements, return all possible subsets (the power set). The solution set must not contain duplicate subsets. Return the solution in any order.",
                        "Find all subsets of array",
                        new String[]{"array", "subsets", "power set", "combinations", "backtracking", "recursive"},
                        "Backtracking",
                        "backtrack.Subsets",
                        new String[]{"computeRecursive", "computeIterative", "computeIncremental"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{1,2,3}})
                };
            
            case 13: // Trie
                return new ProblemInfo[] {
                    new ProblemInfo("Implement Trie",
                        new String[]{"ImplementTrie", "Trie", "Prefix Tree"},
                        "A trie (pronounced as 'try') or prefix tree is a tree data structure used to efficiently store and retrieve keys in a dataset of strings. There are various applications of this data structure, such as autocomplete and spellchecker. Implement the Trie class: Trie() Initializes the trie object. void insert(String word) Inserts the string word into the trie. boolean search(String word) Returns true if the string word is in the trie (i.e., was inserted before), and false otherwise. boolean startsWith(String prefix) Returns true if there is a previously inserted string word that has the prefix prefix, and false otherwise.",
                        "Implement Trie (Prefix Tree) data structure",
                        new String[]{"trie", "prefix tree", "data structure", "insert", "search", "startsWith", "autocomplete"},
                        "Trie",
                        "trie.ImplementTrie",
                        new String[]{"insert", "search", "startsWith"},
                        new String[]{"trie", "word"},
                        new String[]{"ImplementTrie", "String"},
                        null)
                };
            
            case 14: // Intervals
                return new ProblemInfo[] {
                    new ProblemInfo("Merge Intervals",
                        new String[]{"MergeIntervals", "Merge", "Overlapping Intervals"},
                        "Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.",
                        "Merge overlapping intervals",
                        new String[]{"intervals", "merge", "overlapping", "array", "sort", "ranges"},
                        "Intervals",
                        "intervals.MergeIntervals",
                        new String[]{"computeWithSorting", "computeWithArrayList", "computeRecursive"},
                        new String[]{"intervals"},
                        new String[]{"int[][]"},
                        new Object[]{new int[][]{{1,3},{2,6},{8,10},{15,18}}})
                };
            
            case 15: // Math/Geometry
                return new ProblemInfo[] {
                    new ProblemInfo("Pow(x, n)",
                        new String[]{"Pow", "Power", "Exponentiation"},
                        "Implement pow(x, n), which calculates x raised to the power n (i.e., x^n).",
                        "Calculate x raised to power n",
                        new String[]{"math", "power", "exponent", "x^n", "fast power", "binary exponentiation"},
                        "Math/Geometry",
                        "math.Pow",
                        new String[]{"computeRecursive", "computeIterative", "computeBruteForce"},
                        new String[]{"x", "n"},
                        new String[]{"double", "int"},
                        new Object[]{2.0, 10})
                };
            
            case 16: // Greedy
                return new ProblemInfo[] {
                    new ProblemInfo("Jump Game",
                        new String[]{"JumpGame", "Jump", "Can Jump"},
                        "You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position. Return true if you can reach the last index, or false otherwise.",
                        "Check if can reach last index by jumping",
                        new String[]{"array", "jump", "game", "reach", "last index", "greedy", "dp"},
                        "Greedy",
                        "greedy.JumpGame",
                        new String[]{"computeWithGreedy", "computeWithDP", "computeRecursive"},
                        new String[]{"nums"},
                        new String[]{"int[]"},
                        new Object[]{new int[]{2,3,1,1,4}})
                };
            
            case 17: // Bit Manipulation
                return new ProblemInfo[] {
                    new ProblemInfo("Number of 1 Bits",
                        new String[]{"NumberOf1Bits", "Hamming Weight", "Set Bits"},
                        "Write a function that takes the binary representation of an unsigned integer and returns the number of '1' bits it has (also known as the Hamming weight).",
                        "Count number of 1 bits in integer",
                        new String[]{"bit", "bits", "hamming weight", "count", "ones", "set bits", "bit manipulation"},
                        "Bit Manipulation",
                        "bitmanip.NumberOf1Bits",
                        new String[]{"computeWithBuiltIn", "computeWithLoop", "computeWithKernighan", "computeWithShift", "computeRecursive"},
                        new String[]{"n"},
                        new String[]{"int"},
                        new Object[]{11})
                };
            
            default:
                return null;
        }
    }
}
