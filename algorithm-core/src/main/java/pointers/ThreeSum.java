package pointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 3Sum - LeetCode 15
 * Given an integer array nums, return all the triplets [nums[i], nums[j],
 * nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] +
 * nums[k] == 0.
 * The solution set must not contain duplicate triplets.
 */
public class ThreeSum {

    /**
     * Solution 1: Using Two Pointers with Sorting
     * Time Complexity: O(n²)
     * Space Complexity: O(1) excluding output
     */
    public static List<List<Integer>> computeWithTwoPointers(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1])
                        left++;
                    while (left < right && nums[right] == nums[right - 1])
                        right--;
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }

    /**
     * Solution 2: Using HashSet
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static List<List<Integer>> computeWithHashSet(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            java.util.Set<Integer> set = new java.util.HashSet<>();
            for (int j = i + 1; j < nums.length; j++) {
                int complement = -(nums[i] + nums[j]);
                if (set.contains(complement)) {
                    result.add(Arrays.asList(nums[i], complement, nums[j]));
                    while (j + 1 < nums.length && nums[j] == nums[j + 1])
                        j++;
                }
                set.add(nums[j]);
            }
        }
        return result;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n³)
     * Space Complexity: O(1) excluding output
     */
    public static List<List<Integer>> computeWithBruteForce(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1])
                continue;
            for (int j = i + 1; j < nums.length - 1; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1])
                    continue;
                for (int k = j + 1; k < nums.length; k++) {
                    if (k > j + 1 && nums[k] == nums[k - 1])
                        continue;
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(n²)
     * Space Complexity: O(n) for recursion stack
     */
    public static List<List<Integer>> computeWithRecursion(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1])
                continue;
            findTwoSum(nums, i, nums[i], i + 1, result);
        }
        return result;
    }

    private static void findTwoSum(int[] nums, int firstIndex, int firstValue, int start, List<List<Integer>> result) {
        int left = start;
        int right = nums.length - 1;

        while (left < right) {
            int sum = firstValue + nums[left] + nums[right];
            if (sum == 0) {
                result.add(Arrays.asList(firstValue, nums[left], nums[right]));
                while (left < right && nums[left] == nums[left + 1])
                    left++;
                while (left < right && nums[right] == nums[right - 1])
                    right--;
                left++;
                right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }
}

