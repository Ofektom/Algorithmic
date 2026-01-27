package window;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Longest Substring Without Repeating Characters - LeetCode 3
 * Given a string s, find the length of the longest substring without repeating
 * characters.
 */
public class LongestSubstringWithoutRepeating {

    /**
     * Solution 1: Using Sliding Window with HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(min(n, m)) where m is charset size
     */
    public static int computeWithHashSet(String s) {
        Set<Character> set = new HashSet<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            while (set.contains(s.charAt(right))) {
                set.remove(s.charAt(left));
                left++;
            }
            set.add(s.charAt(right));
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    /**
     * Solution 2: Using Sliding Window with HashMap
     * Time Complexity: O(n)
     * Space Complexity: O(min(n, m))
     */
    public static int computeWithHashMap(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c) && map.get(c) >= left) {
                left = map.get(c) + 1;
            }
            map.put(c, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    /**
     * Solution 3: Using StringBuilder for Sliding Window
     * Time Complexity: O(n²) worst case
     * Space Complexity: O(min(n, m))
     */
    public static int computeWithStringBuilder(String s) {
        StringBuilder sb = new StringBuilder();
        int maxLength = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int index = sb.indexOf(String.valueOf(c));
            if (index != -1) {
                sb.delete(0, index + 1);
            }
            sb.append(c);
            maxLength = Math.max(maxLength, sb.length());
        }
        return maxLength;
    }

    /**
     * Solution 4: Using Array (Assuming ASCII characters)
     * Time Complexity: O(n)
     * Space Complexity: O(128) = O(1)
     */
    public static int computeWithArray(String s) {
        int[] lastIndex = new int[128];
        java.util.Arrays.fill(lastIndex, -1);
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastIndex[c] >= left) {
                left = lastIndex[c] + 1;
            }
            lastIndex[c] = right;
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    /**
     * Solution 5: Using Recursion
     * Time Complexity: O(n²)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(String s) {
        return longestSubstringRecursive(s, 0, 0, new HashSet<>());
    }

    private static int longestSubstringRecursive(String s, int start, int end, Set<Character> seen) {
        if (end >= s.length())
            return 0;

        char c = s.charAt(end);
        if (seen.contains(c)) {
            seen.remove(s.charAt(start));
            return longestSubstringRecursive(s, start + 1, end, seen);
        }

        seen.add(c);
        int currentLength = end - start + 1;
        int nextLength = longestSubstringRecursive(s, start, end + 1, seen);
        return Math.max(currentLength, nextLength);
    }
}

