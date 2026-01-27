package window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Substring with Concatenation of All Words - LeetCode 30
 * You are given a string s and an array of strings words. All the strings of words
 * are of the same length. A concatenated substring in s is a substring that contains
 * all the strings of words concatenated in any order. Return the starting indices of
 * all such concatenated substrings in s. You can return the answer in any order.
 */
public class SubstringWithConcatenationOfAllWords {

    /**
     * Solution 1: Sliding Window with HashMap
     * Time Complexity: O(n * m * k) where n is s.length(), m is words.length, k is word length
     * Space Complexity: O(m)
     */
    public static List<Integer> computeWithHashMap(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.length() == 0 || words == null || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int totalLen = words.length * wordLen;
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        for (int i = 0; i <= s.length() - totalLen; i++) {
            Map<String, Integer> seen = new HashMap<>();
            int j = 0;

            while (j < words.length) {
                int wordIndex = i + j * wordLen;
                String word = s.substring(wordIndex, wordIndex + wordLen);

                if (!wordCount.containsKey(word)) {
                    break;
                }

                seen.put(word, seen.getOrDefault(word, 0) + 1);

                if (seen.get(word) > wordCount.get(word)) {
                    break;
                }

                j++;
            }

            if (j == words.length) {
                result.add(i);
            }
        }

        return result;
    }

    /**
     * Solution 2: Sliding Window (Optimized)
     * Time Complexity: O(n * m * k)
     * Space Complexity: O(m)
     */
    public static List<Integer> computeWithSlidingWindow(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.length() == 0 || words == null || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int totalLen = words.length * wordLen;
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        for (int start = 0; start < wordLen; start++) {
            Map<String, Integer> seen = new HashMap<>();
            int left = start;
            int count = 0;

            for (int right = start; right <= s.length() - wordLen; right += wordLen) {
                String word = s.substring(right, right + wordLen);

                if (wordCount.containsKey(word)) {
                    seen.put(word, seen.getOrDefault(word, 0) + 1);
                    count++;

                    while (seen.get(word) > wordCount.get(word)) {
                        String leftWord = s.substring(left, left + wordLen);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        count--;
                        left += wordLen;
                    }

                    if (count == words.length) {
                        result.add(left);
                        String leftWord = s.substring(left, left + wordLen);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        count--;
                        left += wordLen;
                    }
                } else {
                    seen.clear();
                    count = 0;
                    left = right + wordLen;
                }
            }
        }

        return result;
    }
}

