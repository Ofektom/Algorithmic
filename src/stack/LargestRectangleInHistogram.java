package stack;

import java.util.Stack;

/**
 * Largest Rectangle in Histogram - LeetCode 84
 * Given an array of integers heights representing the histogram's bar height
 * where the width of each bar is 1, return the area of the largest rectangle in the histogram.
 */
public class LargestRectangleInHistogram {

    /**
     * Solution 1: Monotonic Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithStack(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int i = 0;

        while (i < heights.length) {
            if (stack.isEmpty() || heights[stack.peek()] <= heights[i]) {
                stack.push(i++);
            } else {
                int top = stack.pop();
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, heights[top] * width);
            }
        }

        while (!stack.isEmpty()) {
            int top = stack.pop();
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, heights[top] * width);
        }

        return maxArea;
    }

    /**
     * Solution 2: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] heights) {
        int maxArea = 0;
        
        for (int i = 0; i < heights.length; i++) {
            int minHeight = heights[i];
            for (int j = i; j < heights.length; j++) {
                minHeight = Math.min(minHeight, heights[j]);
                maxArea = Math.max(maxArea, minHeight * (j - i + 1));
            }
        }
        
        return maxArea;
    }

    /**
     * Solution 3: Divide and Conquer
     * Time Complexity: O(n log n) average, O(n²) worst case
     * Space Complexity: O(log n)
     */
    public static int computeWithDivideConquer(int[] heights) {
        return calculateArea(heights, 0, heights.length - 1);
    }

    private static int calculateArea(int[] heights, int start, int end) {
        if (start > end) return 0;
        
        int minIndex = start;
        for (int i = start; i <= end; i++) {
            if (heights[i] < heights[minIndex]) {
                minIndex = i;
            }
        }
        
        int currentArea = heights[minIndex] * (end - start + 1);
        int leftArea = calculateArea(heights, start, minIndex - 1);
        int rightArea = calculateArea(heights, minIndex + 1, end);
        
        return Math.max(currentArea, Math.max(leftArea, rightArea));
    }
}

