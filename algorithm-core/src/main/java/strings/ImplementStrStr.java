package strings;

/**
 * Implement strStr() - LeetCode 28 (Find Needle in Haystack)
 * Given two strings needle and haystack, return the index of the first occurrence
 * of needle in haystack, or -1 if needle is not part of haystack.
 */
public class ImplementStrStr {

    /**
     * Solution 1: Brute Force (Sliding Window)
     * Time Complexity: O(m * n)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (needle.length() > haystack.length()) return -1;

        int m = haystack.length();
        int n = needle.length();

        for (int i = 0; i <= m - n; i++) {
            int j = 0;
            while (j < n && haystack.charAt(i + j) == needle.charAt(j)) {
                j++;
            }
            if (j == n) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Solution 2: Using String Methods
     * Time Complexity: O(m * n)
     * Space Complexity: O(1)
     */
    public static int computeWithStringMethods(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        return haystack.indexOf(needle);
    }

    /**
     * Solution 3: Using StringBuilder
     * Time Complexity: O(m * n)
     * Space Complexity: O(n)
     */
    public static int computeWithStringBuilder(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (needle.length() > haystack.length()) return -1;

        StringBuilder needleSB = new StringBuilder(needle);
        int n = needle.length();

        for (int i = 0; i <= haystack.length() - n; i++) {
            StringBuilder window = new StringBuilder();
            for (int j = i; j < i + n; j++) {
                window.append(haystack.charAt(j));
            }
            if (window.toString().equals(needleSB.toString())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Solution 4: KMP Algorithm (Knuth-Morris-Pratt)
     * Time Complexity: O(m + n)
     * Space Complexity: O(n)
     */
    public static int computeWithKMP(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (needle.length() > haystack.length()) return -1;

        int[] lps = computeLPS(needle);
        int i = 0; // Index for haystack
        int j = 0; // Index for needle

        while (i < haystack.length()) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
            }

            if (j == needle.length()) {
                return i - j;
            } else if (i < haystack.length() && haystack.charAt(i) != needle.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1;
    }

    private static int[] computeLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}

