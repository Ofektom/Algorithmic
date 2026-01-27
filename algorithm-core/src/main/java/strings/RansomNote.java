package strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Ransom Note - LeetCode 383
 * Given two strings ransomNote and magazine, return true if ransomNote can be
 * constructed by using the letters from magazine and false otherwise.
 * Each letter in magazine can only be used once in ransomNote.
 */
public class RansomNote {

    /**
     * Solution 1: Using HashMap
     * Time Complexity: O(n + m) where n and m are lengths of strings
     * Space Complexity: O(k) where k is number of unique characters
     */
    public static boolean computeWithHashMap(String ransomNote, String magazine) {
        Map<Character, Integer> magazineCount = new HashMap<>();
        for (char c : magazine.toCharArray()) {
            magazineCount.put(c, magazineCount.getOrDefault(c, 0) + 1);
        }

        for (char c : ransomNote.toCharArray()) {
            int count = magazineCount.getOrDefault(c, 0);
            if (count == 0) {
                return false;
            }
            magazineCount.put(c, count - 1);
        }
        return true;
    }

    /**
     * Solution 2: Using Array Counter
     * Time Complexity: O(n + m)
     * Space Complexity: O(1) - fixed size array of 26
     */
    public static boolean computeWithArray(String ransomNote, String magazine) {
        int[] count = new int[26];

        for (char c : magazine.toCharArray()) {
            count[c - 'a']++;
        }

        for (char c : ransomNote.toCharArray()) {
            if (count[c - 'a'] <= 0) {
                return false;
            }
            count[c - 'a']--;
        }
        return true;
    }

    /**
     * Solution 3: Using StringBuilder
     * Time Complexity: O(n * m)
     * Space Complexity: O(m)
     */
    public static boolean computeWithStringBuilder(String ransomNote, String magazine) {
        StringBuilder sb = new StringBuilder(magazine);

        for (char c : ransomNote.toCharArray()) {
            int index = sb.indexOf(String.valueOf(c));
            if (index == -1) {
                return false;
            }
            sb.deleteCharAt(index);
        }
        return true;
    }

    /**
     * Solution 4: Brute Force (Character-by-character check)
     * Time Complexity: O(n * m)
     * Space Complexity: O(1)
     */
    public static boolean computeWithBruteForce(String ransomNote, String magazine) {
        for (char c : ransomNote.toCharArray()) {
            int index = magazine.indexOf(c);
            if (index == -1) {
                return false;
            }
            magazine = magazine.substring(0, index) + magazine.substring(index + 1);
        }
        return true;
    }
}

