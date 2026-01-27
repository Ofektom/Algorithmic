package strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Valid Anagram - LeetCode 242
 * Given two strings s and t, return true if t is an anagram of s, and false
 * otherwise.
 * An Anagram is a word or phrase formed by rearranging the letters of a
 * different word or phrase,
 * typically using all the original letters exactly once.
 */
public class IsAnagram {

    /**
     * Solution 1: Using Sorting
     * Time Complexity: O(n log n) where n is the length of strings
     * Space Complexity: O(1) if we ignore the space used for sorting
     */
    public static boolean computeWithSorting(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();
        Arrays.sort(sArray);
        Arrays.sort(tArray);
        return Arrays.equals(sArray, tArray);
    }

    /**
     * Solution 2: Using HashMap (Counter equivalent)
     * Time Complexity: O(n) where n is the length of strings
     * Space Complexity: O(k) where k is the number of unique characters (at most 26
     * for lowercase letters)
     */
    public static boolean computeWithHashMap(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Integer> countS = new HashMap<>();
        Map<Character, Integer> countT = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            countS.put(s.charAt(i), countS.getOrDefault(s.charAt(i), 0) + 1);
            countT.put(t.charAt(i), countT.getOrDefault(t.charAt(i), 0) + 1);
        }

        for (char c : countS.keySet()) {
            if (!countS.get(c).equals(countT.getOrDefault(c, 0))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Solution 3: Using Single HashMap (More efficient)
     * Time Complexity: O(n) where n is the length of strings
     * Space Complexity: O(k) where k is the number of unique characters
     */
    public static boolean computeWithSingleHashMap(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Integer> count = new HashMap<>();

        // Increment counts for characters in s
        for (char c : s.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }

        // Decrement counts for characters in t
        for (char c : t.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) - 1);
            if (count.get(c) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Solution 4: Using Array Counter (Assuming lowercase English letters only)
     * Time Complexity: O(n) where n is the length of strings
     * Space Complexity: O(1) - fixed size array of 26
     */
    public static boolean computeWithArrayCounter(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] count = new int[26]; // For lowercase 'a' to 'z'

        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
        }

        for (int c : count) {
            if (c != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Solution 5: Using StringBuilder for String Manipulation
     * Time Complexity: O(n log n) where n is the length of strings
     * Space Complexity: O(n) for the StringBuilder objects
     */
    public static boolean computeWithStringBuilder(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        // Convert to char arrays, sort, then use StringBuilder to build sorted strings
        char[] sChars = s.toCharArray();
        char[] tChars = t.toCharArray();
        Arrays.sort(sChars);
        Arrays.sort(tChars);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        for (char c : sChars) {
            sb1.append(c);
        }

        for (char c : tChars) {
            sb2.append(c);
        }

        return sb1.toString().equals(sb2.toString());
    }
}
