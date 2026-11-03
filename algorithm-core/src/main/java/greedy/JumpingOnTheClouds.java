package greedy;

import java.util.Arrays;

/**
 * Jumping on the Clouds
 * Clouds are 0 (safe) or 1 (thundercloud). From any cloud you may jump 1 or 2
 * positions ahead onto a safe cloud. Return the minimum jumps to reach the last
 * cloud. The first and last clouds are always safe.
 */
public class JumpingOnTheClouds {

    /**
     * Solution 1: Greedy (prefer the longer jump)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithGreedy(int[] clouds) {
        int jumps = 0;
        int i = 0;
        while (i < clouds.length - 1) {
            i += (i + 2 < clouds.length && clouds[i + 2] == 0) ? 2 : 1;
            jumps++;
        }
        return jumps;
    }

    /**
     * Solution 2: Dynamic Programming
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithDP(int[] clouds) {
        int n = clouds.length;
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i < n; i++) {
            if (clouds[i] == 1) continue;
            if (dp[i - 1] != Integer.MAX_VALUE) dp[i] = dp[i - 1] + 1;
            if (i >= 2 && dp[i - 2] != Integer.MAX_VALUE) dp[i] = Math.min(dp[i], dp[i - 2] + 1);
        }
        return dp[n - 1];
    }
}
