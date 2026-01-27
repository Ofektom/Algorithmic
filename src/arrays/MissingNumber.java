package arrays;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Missing Number - LeetCode 268
 * Given an array nums containing n distinct numbers in the range [0, n], return
 * the only number in the range that is missing from the array.
 */
public class MissingNumber {

    /**
     * Solution 1: Using HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithHashSet(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        for (int i = 0; i <= nums.length; i++) {
            if (!set.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 2: Using Math (Sum Formula)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithMath(int[] nums) {
        int n = nums.length;
        int expectedSum = n * (n + 1) / 2;
        int actualSum = 0;

        for (int num : nums) {
            actualSum += num;
        }

        return expectedSum - actualSum;
    }

    /**
     * Solution 3: Using XOR (Bit Manipulation)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithXOR(int[] nums) {
        int xor = 0;
        for (int i = 0; i < nums.length; i++) {
            xor ^= (i + 1) ^ nums[i];
        }
        return xor;
    }

    /**
     * Solution 4: Using Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static int computeWithSorting(int[] nums) {
        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i) {
                return i;
            }
        }
        return nums.length;
    }

    /**
     * Solution 5: Using ArrayList
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithArrayList(int[] nums) {
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        for (int num : nums) {
            list.add(num);
        }

        for (int i = 0; i <= nums.length; i++) {
            if (!list.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 6: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }
        return findMissingRecursive(set, 0, nums.length);
    }

    private static int findMissingRecursive(Set<Integer> set, int index, int n) {
        if (index > n) {
            return -1;
        }
        if (!set.contains(index)) {
            return index;
        }
        return findMissingRecursive(set, index + 1, n);
    }
}

