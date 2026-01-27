package strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Group Anagrams - LeetCode 49
 * Given an array of strings strs, group the anagrams together. You can return
 * the answer in any order.
 * An Anagram is a word or phrase formed by rearranging the letters of a
 * different word or phrase.
 */
public class GroupAnagrams {

    /**
     * Solution 1: Using HashMap with Sorted String as Key
     * Time Complexity: O(n * k log k) where n is number of strings, k is max
     * string length
     * Space Complexity: O(n * k)
     */
    public static List<List<String>> computeWithSortedKey(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Solution 2: Using HashMap with StringBuilder for Sorted Key
     * Time Complexity: O(n * k log k)
     * Space Complexity: O(n * k)
     */
    public static List<List<String>> computeWithStringBuilder(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            StringBuilder sb = new StringBuilder();
            for (char c : chars) {
                sb.append(c);
            }
            String key = sb.toString();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Solution 3: Using HashMap with Character Count as Key
     * Time Complexity: O(n * k)
     * Space Complexity: O(n * k)
     */
    public static List<List<String>> computeWithCharCount(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            int[] count = new int[26];
            for (char c : str.toCharArray()) {
                count[c - 'a']++;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append('#');
                sb.append(count[i]);
            }
            String key = sb.toString();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Solution 4: Using Recursion for Character Counting
     * Time Complexity: O(n * k)
     * Space Complexity: O(n * k)
     */
    public static List<List<String>> computeWithRecursion(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            String key = getCharCountKey(str, 0, new int[26]);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }

    private static String getCharCountKey(String str, int index, int[] count) {
        if (index >= str.length()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append('#').append(count[i]);
            }
            return sb.toString();
        }
        count[str.charAt(index) - 'a']++;
        return getCharCountKey(str, index + 1, count);
    }
}

