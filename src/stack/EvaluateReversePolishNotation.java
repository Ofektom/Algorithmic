package stack;

import java.util.Stack;

/**
 * Evaluate Reverse Polish Notation - LeetCode 150
 * You are given an array of strings tokens that represents an arithmetic expression
 * in a Reverse Polish Notation. Evaluate the expression and return an integer that
 * represents the value of the expression.
 * Note that:
 * - The valid operators are '+', '-', '*', and '/'.
 * - Each operand may be an integer or another expression.
 * - The division between two integers always truncates toward zero.
 * - There will not be any division by zero.
 */
public class EvaluateReversePolishNotation {

    /**
     * Solution 1: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithStack(String[] tokens) {
        Stack<Integer> stack = new Stack<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                int b = stack.pop();
                int a = stack.pop();
                int result = performOperation(a, b, token);
                stack.push(result);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("*") || token.equals("/");
    }

    private static int performOperation(int a, int b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> 0;
        };
    }

    /**
     * Solution 2: Using Array (Optimized)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithArray(String[] tokens) {
        int[] stack = new int[tokens.length];
        int top = -1;

        for (String token : tokens) {
            if (isOperator(token)) {
                int b = stack[top--];
                int a = stack[top--];
                stack[++top] = performOperation(a, b, token);
            } else {
                stack[++top] = Integer.parseInt(token);
            }
        }

        return stack[0];
    }

    /**
     * Solution 3: Recursive Approach
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    private static int index;

    public static int computeRecursive(String[] tokens) {
        index = tokens.length - 1;
        return evaluate(tokens);
    }

    private static int evaluate(String[] tokens) {
        String token = tokens[index--];
        if (isOperator(token)) {
            int b = evaluate(tokens);
            int a = evaluate(tokens);
            return performOperation(a, b, token);
        } else {
            return Integer.parseInt(token);
        }
    }
}

