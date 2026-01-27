package strings;

/**
 * Longest Common Prefix - LeetCode 14
 * Write a function to find the longest common prefix string amongst an array of
 * strings.
 * If there is no common prefix, return an empty string "".
 */
public class LongestCommonPrefix {

    /**
     * Solution 1: Horizontal Scanning
     * Time Complexity: O(S) where S is sum of all characters
     * Space Complexity: O(1)
     */
    public static String computeHorizontal(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        return prefix;
    }

    /**
     * Solution 2: Vertical Scanning
     * Time Complexity: O(S)
     * Space Complexity: O(1)
     */
    public static String computeVertical(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        for (int i = 0; i < strs[0].length(); i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                if (i == strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }

    /**
     * Solution 3: Using StringBuilder
     * Time Complexity: O(S)
     * Space Complexity: O(1)
     */
    public static String computeWithStringBuilder(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        StringBuilder prefix = new StringBuilder();
        int minLen = Integer.MAX_VALUE;

        for (String str : strs) {
            minLen = Math.min(minLen, str.length());
        }

        for (int i = 0; i < minLen; i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                if (strs[j].charAt(i) != c) {
                    return prefix.toString();
                }
            }
            prefix.append(c);
        }
        return prefix.toString();
    }

    /**
     * Solution 4: Divide and Conquer
     * Time Complexity: O(S)
     * Space Complexity: O(m log n) where m is length of prefix, n is number of strings
     */
    public static String computeDivideAndConquer(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        return longestCommonPrefix(strs, 0, strs.length - 1);
    }

    private static String longestCommonPrefix(String[] strs, int left, int right) {
        if (left == right) {
            return strs[left];
        }

        int mid = left + (right - left) / 2;
        String leftPrefix = longestCommonPrefix(strs, left, mid);
        String rightPrefix = longestCommonPrefix(strs, mid + 1, right);
        return commonPrefix(leftPrefix, rightPrefix);
    }

    private static String commonPrefix(String left, String right) {
        int minLen = Math.min(left.length(), right.length());
        for (int i = 0; i < minLen; i++) {
            if (left.charAt(i) != right.charAt(i)) {
                return left.substring(0, i);
            }
        }
        return left.substring(0, minLen);
    }

    /**
     * Solution 5: Using Recursion
     * Time Complexity: O(S)
     * Space Complexity: O(S) for recursion stack
     */
    public static String computeWithRecursion(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        return findPrefixRecursive(strs, 0, new StringBuilder());
    }

    private static String findPrefixRecursive(String[] strs, int index, StringBuilder prefix) {
        if (index >= strs[0].length()) {
            return prefix.toString();
        }

        char c = strs[0].charAt(index);
        for (int i = 1; i < strs.length; i++) {
            if (index >= strs[i].length() || strs[i].charAt(index) != c) {
                return prefix.toString();
            }
        }

        prefix.append(c);
        return findPrefixRecursive(strs, index + 1, prefix);
    }

    /**
     * Solution 6: Binary Search on Prefix Length
     * Time Complexity: O(S log m) where m is length of shortest string
     * Space Complexity: O(1)
     */
    public static String computeBinarySearch(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        int minLen = Integer.MAX_VALUE;
        for (String str : strs) {
            minLen = Math.min(minLen, str.length());
        }

        int left = 0;
        int right = minLen;

        while (left < right) {
            int mid = left + (right - left + 1) / 2;
            if (isCommonPrefix(strs, mid)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return strs[0].substring(0, left);
    }

    private static boolean isCommonPrefix(String[] strs, int len) {
        String prefix = strs[0].substring(0, len);
        for (int i = 1; i < strs.length; i++) {
            if (!strs[i].startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }
}

