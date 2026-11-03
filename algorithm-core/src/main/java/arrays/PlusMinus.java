package arrays;

/**
 * Plus Minus
 * Given an integer array, return the ratios of positive, negative and zero
 * elements, each formatted to 6 decimal places.
 */
public class PlusMinus {

    /**
     * Solution 1: Counting
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static String computeWithCounting(int[] arr) {
        int positive = 0, negative = 0, zero = 0;
        for (int num : arr) {
            if (num > 0) positive++;
            else if (num < 0) negative++;
            else zero++;
        }
        return format(positive, negative, zero, arr.length);
    }

    /**
     * Solution 2: Streams
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static String computeWithStreams(int[] arr) {
        long positive = java.util.Arrays.stream(arr).filter(x -> x > 0).count();
        long negative = java.util.Arrays.stream(arr).filter(x -> x < 0).count();
        long zero = arr.length - positive - negative;
        return format(positive, negative, zero, arr.length);
    }

    private static String format(long positive, long negative, long zero, int total) {
        return String.format("%.6f %.6f %.6f",
                (double) positive / total, (double) negative / total, (double) zero / total);
    }
}
