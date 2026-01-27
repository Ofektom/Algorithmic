package pointers;

/**
 * Valid Palindrome - LeetCode 125
 * A phrase is a palindrome if, after converting all uppercase letters into
 * lowercase letters and removing all non-alphanumeric characters, it reads the
 * same forward and backward.
 * Given a string s, return true if it is a palindrome, or false otherwise.
 */
public class ValidPalindrome {

    /**
     * Solution 1: Using Two Pointers
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithTwoPointers(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Solution 2: Using StringBuilder (Reversed String)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStringBuilder(String s) {
        StringBuilder cleaned = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                cleaned.append(Character.toLowerCase(c));
            }
        }

        StringBuilder reversed = new StringBuilder(cleaned);
        reversed.reverse();

        return cleaned.toString().equals(reversed.toString());
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static boolean computeWithRecursion(String s) {
        String cleaned = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return isPalindromeRecursive(cleaned, 0, cleaned.length() - 1);
    }

    private static boolean isPalindromeRecursive(String s, int left, int right) {
        if (left >= right)
            return true;
        if (s.charAt(left) != s.charAt(right))
            return false;
        return isPalindromeRecursive(s, left + 1, right - 1);
    }

    /**
     * Solution 4: Using StringBuilder with Manual Palindrome Check
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStringBuilderManual(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }

        String cleaned = sb.toString();
        int left = 0;
        int right = cleaned.length() - 1;

        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}

