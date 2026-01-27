package arrays;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains Duplicate - LeetCode 217
 * Given an integer array nums, return true if any value appears at least twice
 * in the array, and return false if every element is distinct.
 */
public class ContainsDuplicate {

    /**
     * Solution 1: Using HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithHashSet(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (seen.contains(num)) {
                return true;
            }
            seen.add(num);
        }
        return false;
    }

    /**
     * Solution 2: Using HashSet (Stream approach)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStream(int[] nums) {
        return Arrays.stream(nums).distinct().count() < nums.length;
    }

    /**
     * Solution 3: Using Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithSorting(int[] nums) {
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Solution 4: Brute Force (Linear Search)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static boolean computeWithBruteForce(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) {
                    return true;
                }
            }
        }
        return false;
    }
}

