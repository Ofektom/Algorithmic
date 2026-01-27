package dp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Unique Paths - LeetCode 62
 * There is a robot on an m x n grid. The robot is initially located at the top-left corner (grid[0][0]).
 * The robot tries to move to the bottom-right corner (grid[m - 1][n - 1]).
 * The robot can only move either down or right at any point in time.
 * Given the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.
 */
public class UniquePaths {

    /**
     * Solution 1: Dynamic Programming (2D)
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */
    public static int computeWithDP(int m, int n) {
        int[][] dp = new int[m][n];
        
        for (int i = 0; i < m; i++) dp[i][0] = 1;
        for (int j = 0; j < n; j++) dp[0][j] = 1;
        
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }
        
        return dp[m - 1][n - 1];
    }

    /**
     * Solution 2: Space-Optimized DP
     * Time Complexity: O(m * n)
     * Space Complexity: O(n)
     */
    public static int computeWithOptimizedSpace(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];
            }
        }
        
        return dp[n - 1];
    }

    /**
     * Solution 3: Recursion with Memoization
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */
    public static int computeWithRecursion(int m, int n) {
        Map<String, Integer> memo = new HashMap<>();
        return uniquePathsRecursive(m, n, 0, 0, memo);
    }

    private static int uniquePathsRecursive(int m, int n, int i, int j, Map<String, Integer> memo) {
        if (i == m - 1 && j == n - 1) return 1;
        if (i >= m || j >= n) return 0;
        
        String key = i + "," + j;
        if (memo.containsKey(key)) return memo.get(key);
        
        int paths = uniquePathsRecursive(m, n, i + 1, j, memo) + 
                    uniquePathsRecursive(m, n, i, j + 1, memo);
        memo.put(key, paths);
        return paths;
    }
}

