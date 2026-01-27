package com.algorithm.web.service;

import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class AlgorithmExecutionService {
    
    public Map<String, Object> execute(String className, String methodName, Object[] inputs) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Class<?> clazz = Class.forName(className);
            Method method = findMethod(clazz, methodName, inputs);
            
            if (method == null) {
                result.put("success", false);
                result.put("error", "Method not found: " + methodName);
                return result;
            }
            
            // Convert inputs to match method parameter types
            Object[] convertedInputs = convertInputs(inputs, method.getParameterTypes());
            
            long startTime = System.nanoTime();
            Object executionResult = method.invoke(null, convertedInputs);
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0;
            
            result.put("success", true);
            result.put("result", formatResult(executionResult));
            result.put("executionTime", executionTime);
            
        } catch (ClassNotFoundException e) {
            result.put("success", false);
            result.put("error", "Algorithm class not found: " + className + ". Make sure the algorithm-core module is properly included.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("argument type mismatch")) {
                result.put("error", "Input type mismatch. Please check your input format. " + 
                          "For arrays, use comma-separated values like: 1,2,3,4,5");
            } else {
                result.put("error", "Invalid arguments: " + errorMsg);
            }
            e.printStackTrace();
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Execution error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Convert input objects to match the expected parameter types
     */
    private Object[] convertInputs(Object[] inputs, Class<?>[] parameterTypes) {
        Object[] converted = new Object[inputs.length];
        
        for (int i = 0; i < inputs.length; i++) {
            Object input = inputs[i];
            Class<?> expectedType = parameterTypes[i];
            
            if (expectedType == int[].class) {
                // Convert List or Object[] to int[]
                if (input instanceof List) {
                    List<?> list = (List<?>) input;
                    int[] arr = new int[list.size()];
                    for (int j = 0; j < list.size(); j++) {
                        arr[j] = ((Number) list.get(j)).intValue();
                    }
                    converted[i] = arr;
                } else if (input instanceof Object[]) {
                    Object[] objArr = (Object[]) input;
                    int[] arr = new int[objArr.length];
                    for (int j = 0; j < objArr.length; j++) {
                        arr[j] = ((Number) objArr[j]).intValue();
                    }
                    converted[i] = arr;
                } else {
                    converted[i] = input;
                }
            } else if (expectedType == String[].class) {
                // Convert List or Object[] to String[]
                if (input instanceof List) {
                    List<?> list = (List<?>) input;
                    String[] arr = new String[list.size()];
                    for (int j = 0; j < list.size(); j++) {
                        arr[j] = list.get(j).toString();
                    }
                    converted[i] = arr;
                } else if (input instanceof Object[]) {
                    Object[] objArr = (Object[]) input;
                    String[] arr = new String[objArr.length];
                    for (int j = 0; j < objArr.length; j++) {
                        arr[j] = objArr[j].toString();
                    }
                    converted[i] = arr;
                } else {
                    converted[i] = input;
                }
            } else if (expectedType == char[].class) {
                // Convert List or Object[] to char[]
                if (input instanceof List) {
                    List<?> list = (List<?>) input;
                    char[] arr = new char[list.size()];
                    for (int j = 0; j < list.size(); j++) {
                        Object item = list.get(j);
                        if (item instanceof String && ((String) item).length() == 1) {
                            arr[j] = ((String) item).charAt(0);
                        } else {
                            arr[j] = item.toString().charAt(0);
                        }
                    }
                    converted[i] = arr;
                } else if (input instanceof String) {
                    converted[i] = ((String) input).toCharArray();
                } else {
                    converted[i] = input;
                }
            } else if (expectedType == int[][].class) {
                // Convert nested List or Object[] to int[][]
                if (input instanceof List) {
                    List<?> list = (List<?>) input;
                    int[][] arr = new int[list.size()][];
                    for (int j = 0; j < list.size(); j++) {
                        Object row = list.get(j);
                        if (row instanceof List) {
                            List<?> rowList = (List<?>) row;
                            arr[j] = new int[rowList.size()];
                            for (int k = 0; k < rowList.size(); k++) {
                                arr[j][k] = ((Number) rowList.get(k)).intValue();
                            }
                        } else if (row instanceof Object[]) {
                            Object[] rowArr = (Object[]) row;
                            arr[j] = new int[rowArr.length];
                            for (int k = 0; k < rowArr.length; k++) {
                                arr[j][k] = ((Number) rowArr[k]).intValue();
                            }
                        }
                    }
                    converted[i] = arr;
                } else {
                    converted[i] = input;
                }
            } else if (expectedType == char[][].class) {
                // Convert nested List or Object[] to char[][]
                if (input instanceof List) {
                    List<?> list = (List<?>) input;
                    char[][] arr = new char[list.size()][];
                    for (int j = 0; j < list.size(); j++) {
                        Object row = list.get(j);
                        if (row instanceof List) {
                            List<?> rowList = (List<?>) row;
                            arr[j] = new char[rowList.size()];
                            for (int k = 0; k < rowList.size(); k++) {
                                Object item = rowList.get(k);
                                if (item instanceof String && ((String) item).length() == 1) {
                                    arr[j][k] = ((String) item).charAt(0);
                                } else {
                                    arr[j][k] = item.toString().charAt(0);
                                }
                            }
                        } else if (row instanceof String) {
                            arr[j] = ((String) row).toCharArray();
                        }
                    }
                    converted[i] = arr;
                } else {
                    converted[i] = input;
                }
            } else if (expectedType == int.class) {
                // Convert Number to int
                if (input instanceof Number) {
                    converted[i] = ((Number) input).intValue();
                } else {
                    converted[i] = input;
                }
            } else if (expectedType == List.class) {
                // Keep as List if already a List
                converted[i] = input;
            } else {
                // For other types, pass through
                converted[i] = input;
            }
        }
        
        return converted;
    }
    
    private Method findMethod(Class<?> clazz, String methodName, Object[] inputs) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && 
                method.getParameterCount() == inputs.length) {
                return method;
            }
        }
        return null;
    }
    
    private String formatResult(Object result) {
        if (result == null) return "null";
        if (result instanceof int[]) return Arrays.toString((int[]) result);
        if (result instanceof String[]) return Arrays.toString((String[]) result);
        if (result instanceof char[]) return Arrays.toString((char[]) result);
        if (result instanceof int[][]) return Arrays.deepToString((int[][]) result);
        if (result instanceof char[][]) return Arrays.deepToString((char[][]) result);
        if (result instanceof List) return result.toString();
        return result.toString();
    }
}
