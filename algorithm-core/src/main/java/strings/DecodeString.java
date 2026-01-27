package strings;

import java.util.Stack;

/**
 * Decode String - LeetCode 394
 * Given an encoded string, return its decoded string.
 * The encoding rule is: k[encoded_string], where the encoded_string inside the
 * square brackets is being repeated exactly k times. Note that k is guaranteed
 * to be a positive integer.
 */
public class DecodeString {

    /**
     * Solution 1: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithStack(String s) {
        Stack<Integer> countStack = new Stack<>();
        Stack<StringBuilder> stringStack = new Stack<>();
        StringBuilder currentString = new StringBuilder();
        int k = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0');
            } else if (c == '[') {
                countStack.push(k);
                stringStack.push(currentString);
                currentString = new StringBuilder();
                k = 0;
            } else if (c == ']') {
                StringBuilder decodedString = stringStack.pop();
                int count = countStack.pop();
                for (int i = 0; i < count; i++) {
                    decodedString.append(currentString);
                }
                currentString = decodedString;
            } else {
                currentString.append(c);
            }
        }
        return currentString.toString();
    }

    /**
     * Solution 2: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static String computeWithRecursion(String s) {
        int[] index = { 0 };
        return decodeRecursive(s, index);
    }

    private static String decodeRecursive(String s, int[] index) {
        StringBuilder result = new StringBuilder();
        int k = 0;

        while (index[0] < s.length()) {
            char c = s.charAt(index[0]);

            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0');
                index[0]++;
            } else if (c == '[') {
                index[0]++;
                String decodedString = decodeRecursive(s, index);
                for (int i = 0; i < k; i++) {
                    result.append(decodedString);
                }
                k = 0;
            } else if (c == ']') {
                index[0]++;
                return result.toString();
            } else {
                result.append(c);
                index[0]++;
            }
        }
        return result.toString();
    }

    /**
     * Solution 3: Using StringBuilder with Manual Parsing
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithStringBuilder(String s) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < s.length()) {
            if (Character.isLetter(s.charAt(i))) {
                result.append(s.charAt(i));
                i++;
            } else if (Character.isDigit(s.charAt(i))) {
                int k = 0;
                while (Character.isDigit(s.charAt(i))) {
                    k = k * 10 + (s.charAt(i) - '0');
                    i++;
                }

                i++; // skip '['
                int start = i;
                int bracketCount = 1;

                while (bracketCount > 0) {
                    if (s.charAt(i) == '[') {
                        bracketCount++;
                    } else if (s.charAt(i) == ']') {
                        bracketCount--;
                    }
                    i++;
                }

                String inner = computeWithStringBuilder(s.substring(start, i - 1));
                for (int j = 0; j < k; j++) {
                    result.append(inner);
                }
            } else {
                i++;
            }
        }
        return result.toString();
    }
}

