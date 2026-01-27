package strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Isomorphic Strings - LeetCode 205
 * Given two strings s and t, determine if they are isomorphic.
 * Two strings s and t are isomorphic if the characters in s can be replaced to
 * get t.
 */
public class IsomorphicStrings {

    /**
     * Solution 1: Using Two HashMaps
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithTwoHashMaps(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Character> mapST = new HashMap<>();
        Map<Character, Character> mapTS = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);

            if (mapST.containsKey(c1) && mapST.get(c1) != c2) {
                return false;
            }
            if (mapTS.containsKey(c2) && mapTS.get(c2) != c1) {
                return false;
            }

            mapST.put(c1, c2);
            mapTS.put(c2, c1);
        }
        return true;
    }

    /**
     * Solution 2: Using One HashMap and One HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithHashMapHashSet(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Character> map = new HashMap<>();
        Set<Character> used = new HashSet<>();

        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);

            if (map.containsKey(c1)) {
                if (map.get(c1) != c2) {
                    return false;
                }
            } else {
                if (used.contains(c2)) {
                    return false;
                }
                map.put(c1, c2);
                used.add(c2);
            }
        }
        return true;
    }

    /**
     * Solution 3: Using Array as HashMap (ASCII only)
     * Time Complexity: O(n)
     * Space Complexity: O(1) - fixed size arrays
     */
    public static boolean computeWithArray(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] mapST = new int[256];
        int[] mapTS = new int[256];

        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);

            if (mapST[c1] != 0 && mapST[c1] != c2) {
                return false;
            }
            if (mapTS[c2] != 0 && mapTS[c2] != c1) {
                return false;
            }

            mapST[c1] = c2;
            mapTS[c2] = c1;
        }
        return true;
    }

    /**
     * Solution 4: Using String Transformation
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithTransformation(String s, String t) {
        return transform(s).equals(transform(t));
    }

    private static String transform(String str) {
        Map<Character, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c, map.size());
            }
            sb.append(map.get(c)).append(" ");
        }
        return sb.toString();
    }

    /**
     * Solution 5: Using StringBuilder
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStringBuilder(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Character> map = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);

            if (map.containsKey(c1)) {
                if (map.get(c1) != c2) {
                    return false;
                }
            } else {
                if (map.containsValue(c2)) {
                    return false;
                }
                map.put(c1, c2);
            }
        }
        return true;
    }
}

