package window;

import java.util.Arrays;

/**
 * Permutation in String - LeetCode 567
 * Given two strings s1 and s2, return true if s2 contains a permutation of s1,
 * or false otherwise. In other words, return true if one of s1's permutations
 * is the substring of s2.
 */
public class PermutationInString {

    /**
     * Solution 1: Sliding Window with Array
     * Time Complexity: O(|s1| + |s2|)
     * Space Complexity: O(1)
     */
    public static boolean computeWithArray(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        int[] s1Count = new int[26];
        int[] s2Count = new int[26];

        for (int i = 0; i < s1.length(); i++) {
            s1Count[s1.charAt(i) - 'a']++;
            s2Count[s2.charAt(i) - 'a']++;
        }

        if (Arrays.equals(s1Count, s2Count)) return true;

        for (int i = s1.length(); i < s2.length(); i++) {
            s2Count[s2.charAt(i) - 'a']++;
            s2Count[s2.charAt(i - s1.length()) - 'a']--;

            if (Arrays.equals(s1Count, s2Count)) return true;
        }

        return false;
    }

    /**
     * Solution 2: Sliding Window with HashMap
     * Time Complexity: O(|s1| + |s2|)
     * Space Complexity: O(|s1|)
     */
    public static boolean computeWithHashMap(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        java.util.Map<Character, Integer> s1Map = new java.util.HashMap<>();
        for (char c : s1.toCharArray()) {
            s1Map.put(c, s1Map.getOrDefault(c, 0) + 1);
        }

        java.util.Map<Character, Integer> windowMap = new java.util.HashMap<>();
        int left = 0;

        for (int right = 0; right < s2.length(); right++) {
            char c = s2.charAt(right);
            windowMap.put(c, windowMap.getOrDefault(c, 0) + 1);

            if (right - left + 1 == s1.length()) {
                if (windowMap.equals(s1Map)) return true;

                char leftChar = s2.charAt(left);
                windowMap.put(leftChar, windowMap.get(leftChar) - 1);
                if (windowMap.get(leftChar) == 0) {
                    windowMap.remove(leftChar);
                }
                left++;
            }
        }

        return false;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(|s1|! * |s2|)
     * Space Complexity: O(|s1|)
     */
    public static boolean computeWithBruteForce(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        for (int i = 0; i <= s2.length() - s1.length(); i++) {
            String substring = s2.substring(i, i + s1.length());
            if (isPermutation(s1, substring)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPermutation(String s1, String s2) {
        if (s1.length() != s2.length()) return false;
        char[] arr1 = s1.toCharArray();
        char[] arr2 = s2.toCharArray();
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        return Arrays.equals(arr1, arr2);
    }
}

