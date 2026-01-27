package strings;

import java.util.Stack;

/**
 * Backspace String Compare - LeetCode 844
 * Given two strings s and t, return true if they are equal when both are typed
 * into empty text editors. '#' means a backspace character.
 * Note that after backspacing an empty text, the text will continue empty.
 */
public class BackspaceStringCompare {

    /**
     * Solution 1: Using Stack
     * Time Complexity: O(n + m)
     * Space Complexity: O(n + m)
     */
    public static boolean computeWithStack(String s, String t) {
        return buildString(s).equals(buildString(t));
    }

    private static String buildString(String str) {
        Stack<Character> stack = new Stack<>();
        for (char c : str.toCharArray()) {
            if (c == '#') {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(c);
            }
        }
        return String.valueOf(stack);
    }

    /**
     * Solution 2: Using StringBuilder
     * Time Complexity: O(n + m)
     * Space Complexity: O(n + m)
     */
    public static boolean computeWithStringBuilder(String s, String t) {
        return buildStringStringBuilder(s).equals(buildStringStringBuilder(t));
    }

    private static String buildStringStringBuilder(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == '#') {
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Solution 3: Two Pointers (Reverse Iteration)
     * Time Complexity: O(n + m)
     * Space Complexity: O(1)
     */
    public static boolean computeWithTwoPointers(String s, String t) {
        int i = s.length() - 1;
        int j = t.length() - 1;

        while (i >= 0 || j >= 0) {
            i = getNextValidIndex(s, i);
            j = getNextValidIndex(t, j);

            if (i < 0 && j < 0) {
                return true;
            }
            if (i < 0 || j < 0) {
                return false;
            }
            if (s.charAt(i) != t.charAt(j)) {
                return false;
            }

            i--;
            j--;
        }
        return true;
    }

    private static int getNextValidIndex(String str, int index) {
        int backspaceCount = 0;
        while (index >= 0) {
            if (str.charAt(index) == '#') {
                backspaceCount++;
            } else if (backspaceCount > 0) {
                backspaceCount--;
            } else {
                break;
            }
            index--;
        }
        return index;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(n + m)
     * Space Complexity: O(n + m) for recursion stack
     */
    public static boolean computeWithRecursion(String s, String t) {
        return buildStringRecursive(s, s.length() - 1, new StringBuilder())
                .equals(buildStringRecursive(t, t.length() - 1, new StringBuilder()));
    }

    private static String buildStringRecursive(String str, int index, StringBuilder sb) {
        if (index < 0) {
            return sb.reverse().toString();
        }

        if (str.charAt(index) == '#') {
            return buildStringRecursive(str, index - 1, sb);
        } else {
            sb.append(str.charAt(index));
            return buildStringRecursive(str, index - 1, sb);
        }
    }
}

