package pointers;

/**
 * Container With Most Water - LeetCode 11
 * You are given an integer array height of length n. There are n vertical lines
 * drawn such that the two endpoints of the ith line are (i, 0) and (i,
 * height[i]).
 * Find two lines that together with the x-axis form a container, such that the
 * container contains the most water.
 * Return the maximum amount of water a container can store.
 */
public class ContainerWithMostWater {

    /**
     * Solution 1: Using Two Pointers
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithTwoPointers(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            int width = right - left;
            int minHeight = Math.min(height[left], height[right]);
            maxArea = Math.max(maxArea, width * minHeight);

            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxArea;
    }

    /**
     * Solution 2: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] height) {
        int maxArea = 0;
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                int width = j - i;
                int minHeight = Math.min(height[i], height[j]);
                maxArea = Math.max(maxArea, width * minHeight);
            }
        }
        return maxArea;
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(int[] height) {
        return maxAreaRecursive(height, 0, height.length - 1, 0);
    }

    private static int maxAreaRecursive(int[] height, int left, int right, int maxArea) {
        if (left >= right)
            return maxArea;

        int width = right - left;
        int minHeight = Math.min(height[left], height[right]);
        int currentArea = width * minHeight;
        maxArea = Math.max(maxArea, currentArea);

        if (height[left] < height[right]) {
            return maxAreaRecursive(height, left + 1, right, maxArea);
        } else {
            return maxAreaRecursive(height, left, right - 1, maxArea);
        }
    }
}

