package arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains Duplicate II - LeetCode 219
 * Given an integer array nums and an integer k, return true if there are two distinct
 * indices i and j in the array such that nums[i] == nums[j] and abs(i - j) <= k.
 */
public class ContainsDuplicateII {

    /**
     * Solution 1: Using HashMap (Store index)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithHashMap(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i]) && Math.abs(i - map.get(nums[i])) <= k) {
                return true;
            }
            map.put(nums[i], i);
        }
        return false;
    }

    /**
     * Solution 2: Using HashSet (Sliding Window)
     * Time Complexity: O(n)
     * Space Complexity: O(min(n, k))
     */
    public static boolean computeWithHashSet(int[] nums, int k) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (set.contains(nums[i])) {
                return true;
            }
            set.add(nums[i]);
            if (set.size() > k) {
                set.remove(nums[i - k]);
            }
        }
        return false;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n * k)
     * Space Complexity: O(1)
     */
    public static boolean computeWithBruteForce(int[] nums, int k) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j <= Math.min(i + k, nums.length - 1); j++) {
                if (nums[i] == nums[j]) {
                    return true;
                }
            }
        }
        return false;
    }
}

