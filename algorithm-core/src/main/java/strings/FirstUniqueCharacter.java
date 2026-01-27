package strings;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * First Unique Character in a String - LeetCode 387
 * Given a string s, find the first non-repeating character in it and return its
 * index. If it does not exist, return -1.
 */
public class FirstUniqueCharacter {

    /**
     * Solution 1: Using HashMap (Two Pass)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithHashMap(String s) {
        Map<Character, Integer> count = new HashMap<>();
        for (char c : s.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < s.length(); i++) {
            if (count.get(s.charAt(i)) == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 2: Using LinkedHashMap (Preserves Insertion Order)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithLinkedHashMap(String s) {
        Map<Character, Integer> count = new LinkedHashMap<>();
        for (char c : s.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                return s.indexOf(entry.getKey());
            }
        }
        return -1;
    }

    /**
     * Solution 3: Using Array Counter
     * Time Complexity: O(n)
     * Space Complexity: O(1) - fixed size array of 26 or 128
     */
    public static int computeWithArray(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }

        for (int i = 0; i < s.length(); i++) {
            if (count[s.charAt(i) - 'a'] == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 4: Using String Methods (indexOf, lastIndexOf)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithStringMethods(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (s.indexOf(c) == s.lastIndexOf(c)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 5: Using StringBuilder
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static int computeWithStringBuilder(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            boolean isUnique = true;
            for (int j = 0; j < sb.length(); j++) {
                if (j != i && sb.charAt(j) == c) {
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 6: Using Recursion
     * Time Complexity: O(n²)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(String s) {
        return findFirstUniqueRecursive(s, 0);
    }

    private static int findFirstUniqueRecursive(String s, int index) {
        if (index >= s.length()) {
            return -1;
        }

        char c = s.charAt(index);
        if (countOccurrences(s, c, 0, 0) == 1) {
            return index;
        }

        return findFirstUniqueRecursive(s, index + 1);
    }

    private static int countOccurrences(String s, char target, int index, int count) {
        if (index >= s.length()) {
            return count;
        }
        if (s.charAt(index) == target) {
            count++;
        }
        return countOccurrences(s, target, index + 1, count);
    }
}

