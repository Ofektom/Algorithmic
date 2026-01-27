package strings;

/**
 * Valid Palindrome II - LeetCode 680
 * Given a string s, return true if the s can be palindrome after deleting at
 * most one character from it.
 */
public class ValidPalindromeII {

    /**
     * Solution 1: Two Pointers with Skip
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithTwoPointers(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return isPalindrome(s, left + 1, right) || isPalindrome(s, left, right - 1);
            }
            left++;
            right--;
        }
        return true;
    }

    private static boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Solution 2: Using StringBuilder
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStringBuilder(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                StringBuilder sb1 = new StringBuilder(s);
                sb1.deleteCharAt(left);
                if (isPalindromeStringBuilder(sb1)) {
                    return true;
                }

                StringBuilder sb2 = new StringBuilder(s);
                sb2.deleteCharAt(right);
                return isPalindromeStringBuilder(sb2);
            }
            left++;
            right--;
        }
        return true;
    }

    private static boolean isPalindromeStringBuilder(StringBuilder sb) {
        int left = 0;
        int right = sb.length() - 1;
        while (left < right) {
            if (sb.charAt(left) != sb.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static boolean computeWithRecursion(String s) {
        return isValidPalindromeRecursive(s, 0, s.length() - 1, true);
    }

    private static boolean isValidPalindromeRecursive(String s, int left, int right, boolean canDelete) {
        if (left >= right) {
            return true;
        }

        if (s.charAt(left) == s.charAt(right)) {
            return isValidPalindromeRecursive(s, left + 1, right - 1, canDelete);
        }

        if (!canDelete) {
            return false;
        }

        return isValidPalindromeRecursive(s, left + 1, right, false)
                || isValidPalindromeRecursive(s, left, right - 1, false);
    }

    /**
     * Solution 4: Brute Force (Try deleting each character)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static boolean computeWithBruteForce(String s) {
        if (isPalindrome(s, 0, s.length() - 1)) {
            return true;
        }

        for (int i = 0; i < s.length(); i++) {
            String modified = s.substring(0, i) + s.substring(i + 1);
            if (isPalindrome(modified, 0, modified.length() - 1)) {
                return true;
            }
        }
        return false;
    }
}

