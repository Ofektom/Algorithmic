package backtrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Subsets - LeetCode 78
 * Given an integer array nums of unique elements, return all possible subsets (the power set).
 * The solution set must not contain duplicate subsets. Return the solution in any order.
 */
public class Subsets {

    /**
     * Solution 1: Backtracking (Recursive)
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(2^n * n)
     */
    public static List<List<Integer>> computeRecursive(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }

    private static void backtrack(List<List<Integer>> result, List<Integer> current,
                                   int[] nums, int start) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(result, current, nums, i + 1);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Solution 2: Iterative (Bit Manipulation approach)
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(2^n * n)
     */
    public static List<List<Integer>> computeIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        int totalSubsets = 1 << n; // 2^n

        for (int i = 0; i < totalSubsets; i++) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    subset.add(nums[j]);
                }
            }
            result.add(subset);
        }

        return result;
    }

    /**
     * Solution 3: Iterative (Build incrementally)
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(2^n * n)
     */
    public static List<List<Integer>> computeIncremental(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        for (int num : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                List<Integer> newSubset = new ArrayList<>(result.get(i));
                newSubset.add(num);
                result.add(newSubset);
            }
        }

        return result;
    }
}

