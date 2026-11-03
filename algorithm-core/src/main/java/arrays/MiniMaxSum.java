package arrays;

import java.util.Arrays;

/**
 * Mini-Max Sum
 * Given five positive integers, return the minimum and maximum sums obtainable
 * by adding exactly four of them.
 */
public class MiniMaxSum {

    /**
     * Solution 1: Single Pass (total minus max / min)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static long[] computeWithSinglePass(int[] arr) {
        long total = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int num : arr) {
            total += num;
            min = Math.min(min, num);
            max = Math.max(max, num);
        }
        return new long[] { total - max, total - min };
    }

    /**
     * Solution 2: Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static long[] computeWithSort(int[] arr) {
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        long minSum = 0, maxSum = 0;
        for (int i = 0; i < sorted.length - 1; i++) {
            minSum += sorted[i];
            maxSum += sorted[i + 1];
        }
        return new long[] { minSum, maxSum };
    }
}
