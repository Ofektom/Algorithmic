package strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Reverse Words in a String - LeetCode 151
 * Given an input string s, reverse the order of the words.
 * A word is defined as a sequence of non-space characters. The words in s will
 * be separated by at least one space.
 * Return a string of the words in reverse order concatenated by a single space.
 */
public class ReverseWordsInString {

    /**
     * Solution 1: Using Built-in String Methods
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithBuiltIn(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Solution 2: Using StringBuilder (Manual Parsing)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithStringBuilder(String s) {
        StringBuilder sb = new StringBuilder();
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                }
            } else {
                currentWord.append(c);
            }
        }
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }

        for (int i = words.size() - 1; i >= 0; i--) {
            sb.append(words.get(i));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Solution 3: Two-Pass with StringBuilder
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithTwoPass(String s) {
        s = s.trim();
        StringBuilder sb = new StringBuilder();
        int end = s.length();

        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == ' ') {
                if (i < end - 1) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(s.substring(i + 1, end));
                }
                end = i;
            }
        }

        if (end > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(s.substring(0, end));
        }
        return sb.toString();
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static String computeWithRecursion(String s) {
        s = s.trim();
        return reverseWordsRecursive(s, new StringBuilder(), s.length() - 1, s.length());
    }

    private static String reverseWordsRecursive(String s, StringBuilder result, int index, int end) {
        if (index < 0) {
            if (end > 0) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(s.substring(0, end));
            }
            return result.toString();
        }

        if (s.charAt(index) == ' ') {
            if (index < end - 1) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(s.substring(index + 1, end));
            }
            return reverseWordsRecursive(s, result, index - 1, index);
        }

        return reverseWordsRecursive(s, result, index - 1, end);
    }

    /**
     * Solution 5: In-place with Character Array (if allowed)
     * Time Complexity: O(n)
     * Space Complexity: O(1) excluding output
     */
    public static String computeInPlace(String s) {
        char[] chars = s.toCharArray();
        int n = chars.length;

        // Reverse entire string
        reverse(chars, 0, n - 1);

        // Reverse each word
        int start = 0;
        for (int i = 0; i <= n; i++) {
            if (i == n || chars[i] == ' ') {
                if (start < i) {
                    reverse(chars, start, i - 1);
                }
                start = i + 1;
            }
        }

        // Remove extra spaces
        return cleanSpaces(chars);
    }

    private static void reverse(char[] chars, int left, int right) {
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
    }

    private static String cleanSpaces(char[] chars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != ' ' || (i > 0 && chars[i - 1] != ' ')) {
                sb.append(chars[i]);
            }
        }
        return sb.toString().trim();
    }
}

