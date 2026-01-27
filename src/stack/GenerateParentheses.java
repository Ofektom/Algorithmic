package stack;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate Parentheses - LeetCode 22
 * Given n pairs of parentheses, write a function to generate all combinations of
 * well-formed parentheses.
 */
public class GenerateParentheses {

    /**
     * Solution 1: Backtracking (Recursive)
     * Time Complexity: O(4^n / sqrt(n))
     * Space Complexity: O(4^n / sqrt(n))
     */
    public static List<String> computeRecursive(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private static void backtrack(List<String> result, StringBuilder current,
                                   int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current.toString());
            return;
        }

        if (open < max) {
            current.append('(');
            backtrack(result, current, open + 1, close, max);
            current.deleteCharAt(current.length() - 1);
        }

        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, max);
            current.deleteCharAt(current.length() - 1);
        }
    }

    /**
     * Solution 2: Recursive with String Concatenation
     * Time Complexity: O(4^n / sqrt(n))
     * Space Complexity: O(4^n / sqrt(n))
     */
    public static List<String> computeWithRecursion(int n) {
        List<String> result = new ArrayList<>();
        generate(result, "", 0, 0, n);
        return result;
    }

    private static void generate(List<String> result, String current,
                                 int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current);
            return;
        }

        if (open < max) {
            generate(result, current + "(", open + 1, close, max);
        }

        if (close < open) {
            generate(result, current + ")", open, close + 1, max);
        }
    }

    /**
     * Solution 3: Iterative with Stack
     * Time Complexity: O(4^n / sqrt(n))
     * Space Complexity: O(4^n / sqrt(n))
     */
    public static List<String> computeWithStack(int n) {
        List<String> result = new ArrayList<>();
        java.util.Stack<State> stack = new java.util.Stack<>();
        stack.push(new State("", 0, 0));

        while (!stack.isEmpty()) {
            State state = stack.pop();
            if (state.current.length() == n * 2) {
                result.add(state.current);
            } else {
                if (state.open < n) {
                    stack.push(new State(state.current + "(", state.open + 1, state.close));
                }
                if (state.close < state.open) {
                    stack.push(new State(state.current + ")", state.open, state.close + 1));
                }
            }
        }

        return result;
    }

    private static class State {
        String current;
        int open;
        int close;

        State(String current, int open, int close) {
            this.current = current;
            this.open = open;
            this.close = close;
        }
    }
}

