package strings;

import java.util.Stack;

/**
 * Reverse String - LeetCode 344
 * Write a function that reverses a string. The input string is given as an
 * array of characters s.
 * You must do this by modifying the input array in-place with O(1) extra
 * memory.
 */
public class ReverseString {

    /**
     * Solution 1: Two Pointers (In-place)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithTwoPointers(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left < right) {
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            left++;
            right--;
        }
    }

    /**
     * Solution 2: Using StringBuilder
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithStringBuilder(String s) {
        StringBuilder sb = new StringBuilder(s);
        return sb.reverse().toString();
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static void computeWithRecursion(char[] s) {
        reverseRecursive(s, 0, s.length - 1);
    }

    private static void reverseRecursive(char[] s, int left, int right) {
        if (left >= right) {
            return;
        }
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;
        reverseRecursive(s, left + 1, right - 1);
    }

    /**
     * Solution 4: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static void computeWithStack(char[] s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s) {
            stack.push(c);
        }

        for (int i = 0; i < s.length; i++) {
            s[i] = stack.pop();
        }
    }

    /**
     * Solution 5: Using StringBuilder with Manual Reverse
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithStringBuilderManual(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}

