package window;

import java.util.HashMap;
import java.util.Map;

/**
 * Longest Repeating Character Replacement - LeetCode 424
 * You are given a string s and an integer k. You can choose any character of
 * the string and change it to any other uppercase English letter. You can
 * perform this operation at most k times.
 * Return the length of the longest substring containing the same letter you can
 * get after performing the above operations.
 */
public class LongestRepeatingCharacterReplacement {

    /**
     * Solution 1: Using Sliding Window with HashMap
     * Time Complexity: O(n)
     * Space Complexity: O(1) - at most 26 characters
     */
    public static int computeWithHashMap(String s, int k) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0;
        int maxLength = 0;
        int maxCount = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            count.put(c, count.getOrDefault(c, 0) + 1);
            maxCount = Math.max(maxCount, count.get(c));

            if (right - left + 1 - maxCount > k) {
                count.put(s.charAt(left), count.get(s.charAt(left)) - 1);
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    /**
     * Solution 2: Using Array for Character Count
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithArray(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int maxLength = 0;
        int maxCount = 0;

        for (int right = 0; right < s.length(); right++) {
            count[s.charAt(right) - 'A']++;
            maxCount = Math.max(maxCount, count[s.charAt(right) - 'A']);

            if (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    /**
     * Solution 3: Using StringBuilder for Window
     * Time Complexity: O(n²) worst case
     * Space Complexity: O(n)
     */
    public static int computeWithStringBuilder(String s, int k) {
        StringBuilder window = new StringBuilder();
        int maxLength = 0;
        Map<Character, Integer> count = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            window.append(c);
            count.put(c, count.getOrDefault(c, 0) + 1);

            int maxCount = 0;
            for (int val : count.values()) {
                maxCount = Math.max(maxCount, val);
            }

            while (window.length() - maxCount > k) {
                char leftChar = window.charAt(0);
                window.deleteCharAt(0);
                count.put(leftChar, count.get(leftChar) - 1);
                if (count.get(leftChar) == 0)
                    count.remove(leftChar);
                maxCount = 0;
                for (int val : count.values()) {
                    maxCount = Math.max(maxCount, val);
                }
            }

            maxLength = Math.max(maxLength, window.length());
        }
        return maxLength;
    }

    /**
     * Solution 4: Brute Force
     * Time Complexity: O(n³)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(String s, int k) {
        int maxLength = 0;

        for (int i = 0; i < s.length(); i++) {
            int[] count = new int[26];
            int maxCount = 0;

            for (int j = i; j < s.length(); j++) {
                count[s.charAt(j) - 'A']++;
                maxCount = Math.max(maxCount, count[s.charAt(j) - 'A']);

                if (j - i + 1 - maxCount <= k) {
                    maxLength = Math.max(maxLength, j - i + 1);
                }
            }
        }
        return maxLength;
    }
}

