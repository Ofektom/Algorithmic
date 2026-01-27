package strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Encode and Decode Strings - LeetCode 271
 * Design an algorithm to encode a list of strings to a string. The encoded
 * string is then sent over the network and is decoded back to the original list
 * of strings.
 */
public class EncodeDecodeStrings {

    /**
     * Solution 1: Using Length Prefix with Delimiter
     * Time Complexity: O(n) where n is total characters
     * Space Complexity: O(n)
     */
    public static String encode(List<String> strs) {
        if (strs == null || strs.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str.length()).append('#').append(str);
        }
        return sb.toString();
    }

    public static List<String> decode(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.isEmpty())
            return result;

        int i = 0;
        while (i < s.length()) {
            int delimiterIndex = s.indexOf('#', i);
            int length = Integer.parseInt(s.substring(i, delimiterIndex));
            i = delimiterIndex + 1;
            result.add(s.substring(i, i + length));
            i += length;
        }
        return result;
    }

    /**
     * Solution 2: Using StringBuilder with Length Prefix
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String encodeWithStringBuilder(List<String> strs) {
        if (strs == null || strs.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            StringBuilder lengthSb = new StringBuilder();
            lengthSb.append(str.length());
            sb.append(lengthSb).append('#').append(str);
        }
        return sb.toString();
    }

    public static List<String> decodeWithStringBuilder(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.isEmpty())
            return result;

        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            if (s.charAt(i) == '#') {
                int length = Integer.parseInt(sb.toString());
                sb.setLength(0);
                i++;
                result.add(s.substring(i, i + length));
                i += length;
            } else {
                sb.append(s.charAt(i));
                i++;
            }
        }
        return result;
    }

    /**
     * Solution 3: Using Recursion for Encoding
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String encodeWithRecursion(List<String> strs) {
        if (strs == null || strs.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        encodeRecursive(strs, 0, sb);
        return sb.toString();
    }

    private static void encodeRecursive(List<String> strs, int index, StringBuilder sb) {
        if (index >= strs.size())
            return;
        String str = strs.get(index);
        sb.append(str.length()).append('#').append(str);
        encodeRecursive(strs, index + 1, sb);
    }

    public static List<String> decodeWithRecursion(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.isEmpty())
            return result;
        decodeRecursive(s, 0, result);
        return result;
    }

    private static int decodeRecursive(String s, int index, List<String> result) {
        if (index >= s.length())
            return index;

        int delimiterIndex = s.indexOf('#', index);
        int length = Integer.parseInt(s.substring(index, delimiterIndex));
        index = delimiterIndex + 1;
        result.add(s.substring(index, index + length));
        return decodeRecursive(s, index + length, result);
    }
}

