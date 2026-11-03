package strings;

/**
 * Repeated String
 * A string s is repeated infinitely. Return the number of 'a' characters in the
 * first n characters of the infinite string.
 */
public class RepeatedString {

    /**
     * Solution 1: Count Full Repeats + Remainder
     * Time Complexity: O(|s|)
     * Space Complexity: O(1)
     */
    public static long computeWithMath(String s, long n) {
        long perString = countA(s, s.length());
        long fullRepeats = n / s.length();
        int remainder = (int) (n % s.length());
        return fullRepeats * perString + countA(s, remainder);
    }

    /**
     * Solution 2: Brute Force (walk each character)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static long computeWithBruteForce(String s, long n) {
        long count = 0;
        for (long i = 0; i < n; i++) {
            if (s.charAt((int) (i % s.length())) == 'a') count++;
        }
        return count;
    }

    private static long countA(String s, int length) {
        long count = 0;
        for (int i = 0; i < length; i++) {
            if (s.charAt(i) == 'a') count++;
        }
        return count;
    }
}
