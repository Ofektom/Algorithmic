package dp;

import java.util.HashMap;
import java.util.Map;

/**
 * Climbing Stairs - LeetCode 70
 * You are climbing a staircase. It takes n steps to reach the top.
 * Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?
 */
public class ClimbingStairs {

    /**
     * Solution 1: Dynamic Programming with Array
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithDP(int n) {
        if (n <= 2) return n;
        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    /**
     * Solution 2: Space-Optimized DP
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithOptimizedSpace(int n) {
        if (n <= 2) return n;
        int prev2 = 1;
        int prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
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
    public static int computeWithRecursion(int n) {
        Map<Integer, Integer> memo = new HashMap<>();
        return climbStairsRecursive(n, memo);
    }

    private static int climbStairsRecursive(int n, Map<Integer, Integer> memo) {
        if (n <= 2) return n;
        if (memo.containsKey(n)) return memo.get(n);
        int result = climbStairsRecursive(n - 1, memo) + climbStairsRecursive(n - 2, memo);
        memo.put(n, result);
        return result;
    }

    /**
     * Solution 4: Pure Recursion
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */
    public static int computeWithPureRecursion(int n) {
        if (n <= 2) return n;
        return computeWithPureRecursion(n - 1) + computeWithPureRecursion(n - 2);
    }
}

