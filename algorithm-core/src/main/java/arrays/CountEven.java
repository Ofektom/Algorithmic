package arrays;

import java.util.*;

/**
 * Count Even Numbers - Count the number of even numbers in an array
 * Given an array of integers, return the count of even numbers.
 */
public class CountEven {
    
    /**
     * Solution 1: Iterative Approach
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int compute(int[] nums) {
        int count = 0;
        for (int num : nums) {
            if (num % 2 == 0) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Solution 2: Using Stream (Java 8+)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithStream(int[] nums) {
        return (int) Arrays.stream(nums)
                .filter(num -> num % 2 == 0)
                .count();
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(int[] nums) {
        return countEvenRecursive(nums, 0, 0);
    }

    private static int countEvenRecursive(int[] nums, int index, int count) {
        if (index >= nums.length) {
            return count;
        }
        if (nums[index] % 2 == 0) {
            count++;
        }
        return countEvenRecursive(nums, index + 1, count);
    }

    /**
     * Solution 4: Using While Loop
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithWhile(int[] nums) {
        int count = 0;
        int i = 0;
        while (i < nums.length) {
            if (nums[i] % 2 == 0) {
                count++;
            }
            i++;
        }
        return count;
    }
}

