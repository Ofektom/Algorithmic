package arrays;

/**
 * Maximum Product Subarray - LeetCode 152
 * Given an integer array nums, find a contiguous non-empty subarray within the array
 * that has the largest product, and return the product.
 * The test cases are generated so that the answer will fit in a 32-bit integer.
 * A subarray is a contiguous part of an array.
 */
public class MaximumProductSubarray {

    /**
     * Solution 1: Dynamic Programming (Tracking max and min)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithDP(int[] nums) {
        if (nums.length == 0) return 0;
        
        int maxSoFar = nums[0];
        int minSoFar = nums[0];
        int result = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            int num = nums[i];
            int tempMax = Math.max(num, Math.max(maxSoFar * num, minSoFar * num));
            minSoFar = Math.min(num, Math.min(maxSoFar * num, minSoFar * num));
            maxSoFar = tempMax;
            result = Math.max(result, maxSoFar);
        }
        
        return result;
    }

    /**
     * Solution 2: Using Two Passes (handle negative numbers)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithTwoPasses(int[] nums) {
        if (nums.length == 0) return 0;
        
        int maxProduct = Integer.MIN_VALUE;
        int product = 1;
        
        // Left to right
        for (int num : nums) {
            product *= num;
            maxProduct = Math.max(maxProduct, product);
            if (product == 0) product = 1;
        }
        
        product = 1;
        // Right to left
        for (int i = nums.length - 1; i >= 0; i--) {
            product *= nums[i];
            maxProduct = Math.max(maxProduct, product);
            if (product == 0) product = 1;
        }
        
        return maxProduct;
    }

    /**
     * Solution 3: Brute Force (Nested Loops)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] nums) {
        if (nums.length == 0) return 0;
        
        int maxProduct = nums[0];
        
        for (int i = 0; i < nums.length; i++) {
            int product = 1;
            for (int j = i; j < nums.length; j++) {
                product *= nums[j];
                maxProduct = Math.max(maxProduct, product);
            }
        }
        
        return maxProduct;
    }

    /**
     * Solution 4: Using Recursion with Memoization
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithRecursion(int[] nums) {
        if (nums.length == 0) return 0;
        return maxProductRecursive(nums, 0, nums[0], nums[0], nums[0]);
    }

    private static int maxProductRecursive(int[] nums, int index, int currentMax, int currentMin, int globalMax) {
        if (index == nums.length - 1) return globalMax;
        
        int num = nums[index + 1];
        int newMax = Math.max(num, Math.max(currentMax * num, currentMin * num));
        int newMin = Math.min(num, Math.min(currentMax * num, currentMin * num));
        int newGlobalMax = Math.max(globalMax, newMax);
        
        return maxProductRecursive(nums, index + 1, newMax, newMin, newGlobalMax);
    }
}

