package pointers;

/**
 * Trapping Rain Water - LeetCode 42
 * Given n non-negative integers representing an elevation map where the width of each bar is 1,
 * compute how much water it can trap after raining.
 */
public class TrappingRainWater {

    /**
     * Solution 1: Two Pointers (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithTwoPointers(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        return water;
    }

    /**
     * Solution 2: Dynamic Programming (Prefix/Suffix Max)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithDP(int[] height) {
        if (height.length == 0) return 0;
        
        int n = height.length;
        int[] leftMax = new int[n];
        int[] rightMax = new int[n];
        
        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }
        
        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }
        
        int water = 0;
        for (int i = 0; i < n; i++) {
            water += Math.min(leftMax[i], rightMax[i]) - height[i];
        }
        
        return water;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] height) {
        int water = 0;
        for (int i = 0; i < height.length; i++) {
            int leftMax = 0, rightMax = 0;
            for (int j = i; j >= 0; j--) {
                leftMax = Math.max(leftMax, height[j]);
            }
            for (int j = i; j < height.length; j++) {
                rightMax = Math.max(rightMax, height[j]);
            }
            water += Math.min(leftMax, rightMax) - height[i];
        }
        return water;
    }
}

