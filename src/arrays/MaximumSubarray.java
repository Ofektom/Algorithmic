package arrays;

/**
 * Maximum Subarray - LeetCode 53
 * Given an integer array nums, find the contiguous subarray (containing at least one number)
 * which has the largest sum and return its sum.
 * A subarray is a contiguous part of an array.
 */
public class MaximumSubarray {

    /**
     * Solution 1: Kadane's Algorithm
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithKadane(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }

    /**
     * Solution 2: Dynamic Programming (DP Array)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];
        int maxSum = dp[0];
        
        for (int i = 1; i < n; i++) {
            dp[i] = Math.max(nums[i], dp[i - 1] + nums[i]);
            maxSum = Math.max(maxSum, dp[i]);
        }
        
        return maxSum;
    }

    /**
     * Solution 3: Divide and Conquer
     * Time Complexity: O(n log n)
     * Space Complexity: O(log n) for recursion stack
     */
    public static int computeWithDivideConquer(int[] nums) {
        return maxSubarrayRecursive(nums, 0, nums.length - 1);
    }

    private static int maxSubarrayRecursive(int[] nums, int left, int right) {
        if (left == right) return nums[left];
        
        int mid = left + (right - left) / 2;
        
        int leftMax = maxSubarrayRecursive(nums, left, mid);
        int rightMax = maxSubarrayRecursive(nums, mid + 1, right);
        int crossMax = maxCrossingSubarray(nums, left, mid, right);
        
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }

    private static int maxCrossingSubarray(int[] nums, int left, int mid, int right) {
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }
        
        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }
        
        return leftSum + rightSum;
    }

    /**
     * Solution 4: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        
        for (int i = 0; i < nums.length; i++) {
            int currentSum = 0;
            for (int j = i; j < nums.length; j++) {
                currentSum += nums[j];
                maxSum = Math.max(maxSum, currentSum);
            }
        }
        
        return maxSum;
    }
}

