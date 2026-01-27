package dp;

import java.util.HashMap;
import java.util.Map;

/**
 * House Robber - LeetCode 198
 * You are a professional robber planning to rob houses along a street.
 * Each house has a certain amount of money stashed, the only constraint stopping you
 * from robbing each of them is that adjacent houses have security systems connected
 * and it will automatically contact the police if two adjacent houses were broken into on the same night.
 * Given an integer array nums representing the amount of money of each house,
 * return the maximum amount of money you can rob tonight without alerting the police.
 */
public class HouseRobber {

    /**
     * Solution 1: Dynamic Programming with Array
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithDP(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        
        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }
        
        return dp[nums.length - 1];
    }

    /**
     * Solution 2: Space-Optimized DP
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithOptimizedSpace(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        
        int prev2 = nums[0];
        int prev1 = Math.max(nums[0], nums[1]);
        
        for (int i = 2; i < nums.length; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }

    /**
     * Solution 3: Recursion with Memoization
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithRecursion(int[] nums) {
        Map<Integer, Integer> memo = new HashMap<>();
        return robRecursive(nums, 0, memo);
    }

    private static int robRecursive(int[] nums, int index, Map<Integer, Integer> memo) {
        if (index >= nums.length) return 0;
        if (memo.containsKey(index)) return memo.get(index);
        
        int rob = nums[index] + robRecursive(nums, index + 2, memo);
        int notRob = robRecursive(nums, index + 1, memo);
        int result = Math.max(rob, notRob);
        memo.put(index, result);
        return result;
    }

    /**
     * Solution 4: Pure Recursion
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */
    public static int computeWithPureRecursion(int[] nums) {
        return robPureRecursive(nums, 0);
    }

    private static int robPureRecursive(int[] nums, int index) {
        if (index >= nums.length) return 0;
        int rob = nums[index] + robPureRecursive(nums, index + 2);
        int notRob = robPureRecursive(nums, index + 1);
        return Math.max(rob, notRob);
    }
}

