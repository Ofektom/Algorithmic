package arrays;

/**
 * Product of Array Except Self - LeetCode 238
 * Given an integer array nums, return an array answer such that answer[i] is
 * equal to the product of all the elements of nums except nums[i].
 * The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit
 * integer.
 * You must write an algorithm that runs in O(n) time and without using the
 * division operator.
 */
public class ProductExceptSelf {

    /**
     * Solution 1: Using Prefix and Suffix Arrays
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithPrefixSuffix(int[] nums) {
        int n = nums.length;
        int[] prefix = new int[n];
        int[] suffix = new int[n];
        int[] result = new int[n];

        prefix[0] = 1;
        for (int i = 1; i < n; i++) {
            prefix[i] = prefix[i - 1] * nums[i - 1];
        }

        suffix[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            suffix[i] = suffix[i + 1] * nums[i + 1];
        }

        for (int i = 0; i < n; i++) {
            result[i] = prefix[i] * suffix[i];
        }
        return result;
    }

    /**
     * Solution 2: Using Output Array Only (Space Optimized)
     * Time Complexity: O(n)
     * Space Complexity: O(1) excluding output array
     */
    public static int[] computeWithOptimizedSpace(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }

        int right = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= right;
            right *= nums[i];
        }

        return result;
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int[] computeWithRecursion(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        productRecursive(nums, result, 0, 1, 1);
        return result;
    }

    private static int productRecursive(int[] nums, int[] result, int index, int left, int right) {
        if (index == nums.length) {
            return 1;
        }
        int currentRight = productRecursive(nums, result, index + 1, left * nums[index], right);
        result[index] = left * currentRight;
        return currentRight * nums[index];
    }

    /**
     * Solution 4: Brute Force (Not optimal - violates O(n) requirement)
     * Time Complexity: O(n²)
     * Space Complexity: O(1) excluding output array
     */
    public static int[] computeWithBruteForce(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            int product = 1;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    product *= nums[j];
                }
            }
            result[i] = product;
        }
        return result;
    }
}

