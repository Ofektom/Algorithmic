package strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Find All Anagrams in a String - LeetCode 438
 * Given two strings s and p, return an array of all the start indices of p's
 * anagrams in s. You may return the answer in any order.
 */
public class FindAllAnagramsInString {

    /**
     * Solution 1: Sliding Window with HashMap
     * Time Complexity: O(n) where n is length of s
     * Space Complexity: O(k) where k is number of unique characters in p
     */
    public static List<Integer> computeWithHashMap(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }

        Map<Character, Integer> pMap = new HashMap<>();
        for (char c : p.toCharArray()) {
            pMap.put(c, pMap.getOrDefault(c, 0) + 1);
        }

        Map<Character, Integer> sMap = new HashMap<>();
        int windowSize = p.length();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sMap.put(c, sMap.getOrDefault(c, 0) + 1);

            if (i >= windowSize) {
                char leftChar = s.charAt(i - windowSize);
                sMap.put(leftChar, sMap.get(leftChar) - 1);
                if (sMap.get(leftChar) == 0) {
                    sMap.remove(leftChar);
                }
            }

            if (i >= windowSize - 1 && sMap.equals(pMap)) {
                result.add(i - windowSize + 1);
            }
        }
        return result;
    }

    /**
     * Solution 2: Sliding Window with Array
     * Time Complexity: O(n)
     * Space Complexity: O(1) - fixed size array of 26
     */
    public static List<Integer> computeWithArray(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }

        int[] pCount = new int[26];
        int[] sCount = new int[26];

        for (int i = 0; i < p.length(); i++) {
            pCount[p.charAt(i) - 'a']++;
            sCount[s.charAt(i) - 'a']++;
        }

        if (matches(pCount, sCount)) {
            result.add(0);
        }

        for (int i = p.length(); i < s.length(); i++) {
            sCount[s.charAt(i) - 'a']++;
            sCount[s.charAt(i - p.length()) - 'a']--;

            if (matches(pCount, sCount)) {
                result.add(i - p.length() + 1);
            }
        }
        return result;
    }

    private static boolean matches(int[] a, int[] b) {
        for (int i = 0; i < 26; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Solution 3: Using StringBuilder with Sorting
     * Time Complexity: O(n * k log k) where k is length of p
     * Space Complexity: O(k)
     */
    public static List<Integer> computeWithStringBuilder(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }

        char[] pChars = p.toCharArray();
        java.util.Arrays.sort(pChars);
        StringBuilder sortedP = new StringBuilder();
        for (char c : pChars) {
            sortedP.append(c);
        }

        for (int i = 0; i <= s.length() - p.length(); i++) {
            String window = s.substring(i, i + p.length());
            char[] windowChars = window.toCharArray();
            java.util.Arrays.sort(windowChars);
            StringBuilder sortedWindow = new StringBuilder();
            for (char c : windowChars) {
                sortedWindow.append(c);
            }

            if (sortedWindow.toString().equals(sortedP.toString())) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Solution 4: Brute Force
     * Time Complexity: O(n * k log k)
     * Space Complexity: O(k)
     */
    public static List<Integer> computeWithBruteForce(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }

        char[] pChars = p.toCharArray();
        java.util.Arrays.sort(pChars);
        String sortedP = new String(pChars);

        for (int i = 0; i <= s.length() - p.length(); i++) {
            String window = s.substring(i, i + p.length());
            char[] windowChars = window.toCharArray();
            java.util.Arrays.sort(windowChars);
            if (new String(windowChars).equals(sortedP)) {
                result.add(i);
            }
        }
        return result;
    }
}

