package window;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimum Window Substring - LeetCode 76
 * Given two strings s and t, return the minimum window substring of s such that
 * every character in t (including duplicates) is included in the window.
 * If there is no such substring, return the empty string "".
 */
public class MinimumWindowSubstring {

    /**
     * Solution 1: Sliding Window with HashMap
     * Time Complexity: O(|s| + |t|)
     * Space Complexity: O(|s| + |t|)
     */
    public static String computeWithHashMap(String s, String t) {
        if (s.length() == 0 || t.length() == 0 || s.length() < t.length()) {
            return "";
        }

        Map<Character, Integer> tMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            tMap.put(c, tMap.getOrDefault(c, 0) + 1);
        }

        int required = tMap.size();
        int left = 0, right = 0;
        int formed = 0;

        Map<Character, Integer> windowMap = new HashMap<>();
        int[] result = {-1, 0, 0}; // {window length, left, right}

        while (right < s.length()) {
            char c = s.charAt(right);
            windowMap.put(c, windowMap.getOrDefault(c, 0) + 1);

            if (tMap.containsKey(c) && windowMap.get(c).intValue() == tMap.get(c).intValue()) {
                formed++;
            }

            while (left <= right && formed == required) {
                c = s.charAt(left);
                if (result[0] == -1 || right - left + 1 < result[0]) {
                    result[0] = right - left + 1;
                    result[1] = left;
                    result[2] = right;
                }

                windowMap.put(c, windowMap.get(c) - 1);
                if (tMap.containsKey(c) && windowMap.get(c).intValue() < tMap.get(c).intValue()) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return result[0] == -1 ? "" : s.substring(result[1], result[2] + 1);
    }

    /**
     * Solution 2: Sliding Window with Array
     * Time Complexity: O(|s| + |t|)
     * Space Complexity: O(128) = O(1)
     */
    public static String computeWithArray(String s, String t) {
        if (s.length() == 0 || t.length() == 0 || s.length() < t.length()) {
            return "";
        }

        int[] tCount = new int[128];
        for (char c : t.toCharArray()) {
            tCount[c]++;
        }

        int required = 0;
        for (int count : tCount) {
            if (count > 0) required++;
        }

        int[] windowCount = new int[128];
        int left = 0, right = 0;
        int formed = 0;

        int[] result = {-1, 0, 0};

        while (right < s.length()) {
            char c = s.charAt(right);
            windowCount[c]++;

            if (tCount[c] > 0 && windowCount[c] == tCount[c]) {
                formed++;
            }

            while (left <= right && formed == required) {
                c = s.charAt(left);
                if (result[0] == -1 || right - left + 1 < result[0]) {
                    result[0] = right - left + 1;
                    result[1] = left;
                    result[2] = right;
                }

                windowCount[c]--;
                if (tCount[c] > 0 && windowCount[c] < tCount[c]) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return result[0] == -1 ? "" : s.substring(result[1], result[2] + 1);
    }
}

