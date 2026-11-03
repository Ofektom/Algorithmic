package arrays;

import java.util.Arrays;

/**
 * Birthday Cake Candles
 * Given candle heights, return how many candles are the tallest.
 */
public class BirthdayCakeCandles {

    /**
     * Solution 1: Single Pass
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithSinglePass(int[] candles) {
        int tallest = Integer.MIN_VALUE;
        int count = 0;
        for (int height : candles) {
            if (height > tallest) {
                tallest = height;
                count = 1;
            } else if (height == tallest) {
                count++;
            }
        }
        return count;
    }

    /**
     * Solution 2: Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithSort(int[] candles) {
        int[] sorted = candles.clone();
        Arrays.sort(sorted);
        int count = 0;
        for (int i = sorted.length - 1; i >= 0 && sorted[i] == sorted[sorted.length - 1]; i--) {
            count++;
        }
        return count;
    }
}
