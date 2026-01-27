package window;

/**
 * Minimum Size Subarray Sum - LeetCode 209
 * Given an array of positive integers nums and a positive integer target, return the
 * minimal length of a contiguous subarray [numsl, numsl+1, ..., numsr-1, numsr] of which
 * the sum is greater than or equal to target. If there is no such subarray, return 0 instead.
 */
public class MinimumSizeSubarraySum {

    /**
     * Solution 1: Sliding Window (Two Pointers)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithSlidingWindow(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int minLength = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (sum >= target) {
                minLength = Math.min(minLength, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    /**
     * Solution 2: Binary Search on Prefix Sum
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithBinarySearch(int target, int[] nums) {
        int n = nums.length;
        int[] prefixSum = new int[n + 1];
        
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        int minLength = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            int toFind = target + prefixSum[i];
            int found = java.util.Arrays.binarySearch(prefixSum, toFind);
            if (found < 0) {
                found = -(found + 1);
            }
            if (found <= n) {
                minLength = Math.min(minLength, found - i);
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int target, int[] nums) {
        int minLength = Integer.MAX_VALUE;

        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum >= target) {
                    minLength = Math.min(minLength, j - i + 1);
                    break;
                }
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }
}

