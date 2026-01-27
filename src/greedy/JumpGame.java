package greedy;

/**
 * Jump Game - LeetCode 55
 * You are given an integer array nums. You are initially positioned at the array's first index,
 * and each element in the array represents your maximum jump length at that position.
 * Return true if you can reach the last index, or false otherwise.
 */
public class JumpGame {

    /**
     * Solution 1: Greedy (Track Maximum Reachable Index)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithGreedy(int[] nums) {
        int maxReach = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) {
                return false;
            }
            maxReach = Math.max(maxReach, i + nums[i]);
            if (maxReach >= nums.length - 1) {
                return true;
            }
        }

        return true;
    }

    /**
     * Solution 2: Dynamic Programming (Bottom-Up)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static boolean computeWithDP(int[] nums) {
        int n = nums.length;
        boolean[] dp = new boolean[n];
        dp[n - 1] = true;

        for (int i = n - 2; i >= 0; i--) {
            int furthestJump = Math.min(i + nums[i], n - 1);
            for (int j = i + 1; j <= furthestJump; j++) {
                if (dp[j]) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[0];
    }

    /**
     * Solution 3: Backtracking (Recursive)
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */
    public static boolean computeRecursive(int[] nums) {
        return canJumpFromPosition(0, nums);
    }

    private static boolean canJumpFromPosition(int position, int[] nums) {
        if (position == nums.length - 1) {
            return true;
        }

        int furthestJump = Math.min(position + nums[position], nums.length - 1);
        for (int nextPosition = position + 1; nextPosition <= furthestJump; nextPosition++) {
            if (canJumpFromPosition(nextPosition, nums)) {
                return true;
            }
        }

        return false;
    }
}

