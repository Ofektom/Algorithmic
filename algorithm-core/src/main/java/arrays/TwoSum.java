package arrays;

import java.util.HashMap;
import java.util.Map;

/**
 * Two Sum - LeetCode 1
 * Given an array of integers nums and an integer target, return indices of the
 * two numbers such that they add up to target.
 * You may assume that each input would have exactly one solution, and you may
 * not use the same element twice.
 * You can return the answer in any order.
 */
public class TwoSum {

    /**
     * Solution 1: Using HashMap (One Pass)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithHashMap(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }
        return new int[] {};
    }

    /**
     * Solution 2: Using HashMap (Two Pass)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithHashMapTwoPass(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement) && map.get(complement) != i) {
                return new int[] { i, map.get(complement) };
            }
        }
        return new int[] {};
    }

    /**
     * Solution 3: Brute Force (Nested Loops)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int[] computeWithBruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[] { i, j };
                }
            }
        }
        return new int[] {};
    }
}

