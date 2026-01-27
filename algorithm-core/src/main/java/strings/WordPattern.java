package strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Word Pattern - LeetCode 290
 * Given a pattern and a string s, find if s follows the same pattern.
 * Here follow means a full match, such that there is a bijection between a
 * letter in pattern and a non-empty word in s.
 */
public class WordPattern {

    /**
     * Solution 1: Using HashMap
     * Time Complexity: O(n) where n is number of words
     * Space Complexity: O(k) where k is number of unique characters
     */
    public static boolean computeWithHashMap(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) {
            return false;
        }

        Map<Character, String> map = new HashMap<>();
        Set<String> used = new HashSet<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String word = words[i];

            if (map.containsKey(c)) {
                if (!map.get(c).equals(word)) {
                    return false;
                }
            } else {
                if (used.contains(word)) {
                    return false;
                }
                map.put(c, word);
                used.add(word);
            }
        }
        return true;
    }

    /**
     * Solution 2: Using Two HashMaps
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithTwoHashMaps(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) {
            return false;
        }

        Map<Character, String> charToWord = new HashMap<>();
        Map<String, Character> wordToChar = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String word = words[i];

            if (charToWord.containsKey(c) && !charToWord.get(c).equals(word)) {
                return false;
            }
            if (wordToChar.containsKey(word) && wordToChar.get(word) != c) {
                return false;
            }

            charToWord.put(c, word);
            wordToChar.put(word, c);
        }
        return true;
    }

    /**
     * Solution 3: Using StringBuilder
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStringBuilder(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) {
            return false;
        }

        Map<Character, String> map = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String word = words[i];

            if (map.containsKey(c)) {
                if (!map.get(c).equals(word)) {
                    return false;
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (String value : map.values()) {
                    sb.append(value).append(",");
                }
                if (sb.toString().contains(word)) {
                    return false;
                }
                map.put(c, word);
            }
        }
        return true;
    }

    /**
     * Solution 4: Using Pattern Transformation
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithTransformation(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) {
            return false;
        }

        return transformPattern(pattern).equals(transformWords(words));
    }

    private static String transformPattern(String pattern) {
        Map<Character, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c, map.size());
            }
            sb.append(map.get(c)).append(" ");
        }
        return sb.toString();
    }

    private static String transformWords(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (!map.containsKey(word)) {
                map.put(word, map.size());
            }
            sb.append(map.get(word)).append(" ");
        }
        return sb.toString();
    }
}

