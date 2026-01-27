package strings;

/**
 * Longest Palindromic Substring - LeetCode 5
 * Given a string s, return the longest palindromic substring in s.
 */
public class LongestPalindromicSubstring {

    /**
     * Solution 1: Expand Around Centers
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static String computeExpandAroundCenters(String s) {
        if (s == null || s.length() < 1) {
            return "";
        }

        int start = 0;
        int end = 0;

        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);
            int len2 = expandAroundCenter(s, i, i + 1);
            int len = Math.max(len1, len2);

            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        return s.substring(start, end + 1);
    }

    private static int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    /**
     * Solution 2: Using StringBuilder
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static String computeWithStringBuilder(String s) {
        if (s == null || s.length() < 1) {
            return "";
        }

        String longest = "";

        for (int i = 0; i < s.length(); i++) {
            // Odd length palindromes
            StringBuilder sb1 = new StringBuilder();
            expandPalindrome(s, i, i, sb1);
            if (sb1.length() > longest.length()) {
                longest = sb1.toString();
            }

            // Even length palindromes
            StringBuilder sb2 = new StringBuilder();
            expandPalindrome(s, i, i + 1, sb2);
            if (sb2.length() > longest.length()) {
                longest = sb2.toString();
            }
        }
        return longest;
    }

    private static void expandPalindrome(String s, int left, int right, StringBuilder sb) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            if (left == right) {
                sb.append(s.charAt(left));
            } else {
                sb.insert(0, s.charAt(left));
                sb.append(s.charAt(right));
            }
            left--;
            right++;
        }
    }

    /**
     * Solution 3: Dynamic Programming
     * Time Complexity: O(n²)
     * Space Complexity: O(n²)
     */
    public static String computeWithDP(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int start = 0;
        int maxLen = 1;

        // Every single character is a palindrome
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }

        // Check for palindromes of length 2
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                start = i;
                maxLen = 2;
            }
        }

        // Check for palindromes of length 3 or more
        for (int len = 3; len <= n; len++) {
            for (int i = 0; i < n - len + 1; i++) {
                int j = i + len - 1;
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    start = i;
                    maxLen = len;
                }
            }
        }
        return s.substring(start, start + maxLen);
    }

    /**
     * Solution 4: Brute Force
     * Time Complexity: O(n³)
     * Space Complexity: O(1)
     */
    public static String computeWithBruteForce(String s) {
        String longest = "";
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                String substr = s.substring(i, j);
                if (isPalindrome(substr) && substr.length() > longest.length()) {
                    longest = substr;
                }
            }
        }
        return longest;
    }

    private static boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;
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
     * Solution 5: Using Recursion
     * Time Complexity: O(n²)
     * Space Complexity: O(n) for recursion stack
     */
    public static String computeWithRecursion(String s) {
        if (s == null || s.length() < 1) {
            return "";
        }
        String[] result = { "" };
        findLongestPalindrome(s, 0, result);
        return result[0];
    }

    private static void findLongestPalindrome(String s, int index, String[] result) {
        if (index >= s.length()) {
            return;
        }

        // Check odd length palindromes
        String odd = expandPalindromeRecursive(s, index, index);
        if (odd.length() > result[0].length()) {
            result[0] = odd;
        }

        // Check even length palindromes
        String even = expandPalindromeRecursive(s, index, index + 1);
        if (even.length() > result[0].length()) {
            result[0] = even;
        }

        findLongestPalindrome(s, index + 1, result);
    }

    private static String expandPalindromeRecursive(String s, int left, int right) {
        if (left < 0 || right >= s.length() || s.charAt(left) != s.charAt(right)) {
            return "";
        }
        String inner = expandPalindromeRecursive(s, left - 1, right + 1);
        if (left == right) {
            return s.charAt(left) + inner;
        } else {
            return s.charAt(left) + inner + s.charAt(right);
        }
    }
}

