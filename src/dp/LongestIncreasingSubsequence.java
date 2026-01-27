package dp;

import java.util.Arrays;

/**
 * Longest Increasing Subsequence - LeetCode 300
 * Given an integer array nums, return the length of the longest strictly increasing subsequence.
 */
public class LongestIncreasingSubsequence {

    /**
     * Solution 1: Dynamic Programming
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static int computeWithDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        int maxLen = 0;
        for (int len : dp) {
            maxLen = Math.max(maxLen, len);
        }

        return maxLen;
    }

    /**
     * Solution 2: Binary Search with Patience Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithBinarySearch(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int num : nums) {
            int left = 0, right = size;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (tails[mid] < num) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
            tails[left] = num;
            if (left == size) size++;
        }

        return size;
    }

    /**
     * Solution 3: Recursion with Memoization
     * Time Complexity: O(n²)
     * Space Complexity: O(n²)
     */
    public static int computeRecursive(int[] nums) {
        int[][] memo = new int[nums.length][nums.length + 1];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }
        return lisRecursive(nums, 0, -1, memo);
    }

    private static int lisRecursive(int[] nums, int index, int prevIndex, int[][] memo) {
        if (index == nums.length) {
            return 0;
        }

        if (memo[index][prevIndex + 1] != -1) {
            return memo[index][prevIndex + 1];
        }

        int take = 0;
        if (prevIndex == -1 || nums[index] > nums[prevIndex]) {
            take = 1 + lisRecursive(nums, index + 1, index, memo);
        }

        int notTake = lisRecursive(nums, index + 1, prevIndex, memo);

        memo[index][prevIndex + 1] = Math.max(take, notTake);
        return memo[index][prevIndex + 1];
    }
}

