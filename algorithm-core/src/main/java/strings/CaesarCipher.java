package strings;

/**
 * Caesar Cipher
 * Shift every letter in s by k positions in the alphabet, wrapping around and
 * preserving case. Non-letter characters are unchanged.
 */
public class CaesarCipher {

    /**
     * Solution 1: Character Arithmetic
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithShift(String s, int k) {
        int shift = k % 26;
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append((char) ('A' + (c - 'A' + shift) % 26));
            } else if (Character.isLowerCase(c)) {
                sb.append((char) ('a' + (c - 'a' + shift) % 26));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Solution 2: Rotated Alphabet Lookup
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static String computeWithLookup(String s, int k) {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        int shift = k % 26;
        String rotated = lower.substring(shift) + lower.substring(0, shift);
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            int index = lower.indexOf(Character.toLowerCase(c));
            if (index < 0 || !Character.isLetter(c)) {
                sb.append(c);
            } else {
                char mapped = rotated.charAt(index);
                sb.append(Character.isUpperCase(c) ? Character.toUpperCase(mapped) : mapped);
            }
        }
        return sb.toString();
    }
}
