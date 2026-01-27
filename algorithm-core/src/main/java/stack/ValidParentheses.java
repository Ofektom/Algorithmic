package stack;

import java.util.Stack;

/**
 * Valid Parentheses - LeetCode 20
 * Given a string s containing just the characters '(', ')', '{', '}', '[' and
 * ']', determine if the input string is valid.
 * An input string is valid if:
 * 1. Open brackets must be closed by the same type of brackets.
 * 2. Open brackets must be closed in the correct order.
 * 3. Every close bracket has a corresponding open bracket of the same type.
 */
public class ValidParentheses {

    /**
     * Solution 1: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStack(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else {
                if (stack.isEmpty())
                    return false;
                char top = stack.pop();
                if ((c == ')' && top != '(') || (c == '}' && top != '{') || (c == ']' && top != '[')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * Solution 2: Using Stack with HashMap
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithHashMap(String s) {
        Stack<Character> stack = new Stack<>();
        java.util.Map<Character, Character> map = new java.util.HashMap<>();
        map.put(')', '(');
        map.put('}', '{');
        map.put(']', '[');

        for (char c : s.toCharArray()) {
            if (!map.containsKey(c)) {
                stack.push(c);
            } else {
                if (stack.isEmpty() || stack.pop() != map.get(c)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * Solution 3: Using Array as Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithArray(String s) {
        char[] stack = new char[s.length()];
        int top = -1;

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack[++top] = c;
            } else {
                if (top == -1)
                    return false;
                char last = stack[top--];
                if ((c == ')' && last != '(') || (c == '}' && last != '{') || (c == ']' && last != '[')) {
                    return false;
                }
            }
        }
        return top == -1;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static boolean computeWithRecursion(String s) {
        return isValidRecursive(s, 0, new StringBuilder());
    }

    private static boolean isValidRecursive(String s, int index, StringBuilder stack) {
        if (index >= s.length()) {
            return stack.length() == 0;
        }

        char c = s.charAt(index);
        if (c == '(' || c == '{' || c == '[') {
            stack.append(c);
            return isValidRecursive(s, index + 1, stack);
        } else {
            if (stack.length() == 0)
                return false;
            char last = stack.charAt(stack.length() - 1);
            if ((c == ')' && last != '(') || (c == '}' && last != '{') || (c == ']' && last != '[')) {
                return false;
            }
            stack.setLength(stack.length() - 1);
            return isValidRecursive(s, index + 1, stack);
        }
    }

    /**
     * Solution 5: Using StringBuilder as Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStringBuilder(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                sb.append(c);
            } else {
                if (sb.length() == 0)
                    return false;
                char last = sb.charAt(sb.length() - 1);
                if ((c == ')' && last != '(') || (c == '}' && last != '{') || (c == ']' && last != '[')) {
                    return false;
                }
                sb.setLength(sb.length() - 1);
            }
        }
        return sb.length() == 0;
    }
}

