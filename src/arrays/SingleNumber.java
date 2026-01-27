package arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Single Number - LeetCode 136
 * Given a non-empty array of integers nums, every element appears twice except
 * for one. Find that single one.
 * You must implement a solution with a linear runtime complexity and use only
 * constant extra space.
 */
public class SingleNumber {

    /**
     * Solution 1: Using XOR (Bit Manipulation) - Optimal
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithXOR(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    /**
     * Solution 2: Using HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithHashSet(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            if (set.contains(num)) {
                set.remove(num);
            } else {
                set.add(num);
            }
        }
        return set.iterator().next();
    }

    /**
     * Solution 3: Using HashMap (Count)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithHashMap(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : nums) {
            count.put(num, count.getOrDefault(num, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Solution 4: Using ArrayList
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static int computeWithArrayList(int[] nums) {
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        for (int num : nums) {
            if (list.contains(num)) {
                list.remove(Integer.valueOf(num));
            } else {
                list.add(num);
            }
        }
        return list.get(0);
    }

    /**
     * Solution 5: Using Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static int computeWithSorting(int[] nums) {
        java.util.Arrays.sort(nums);
        for (int i = 0; i < nums.length - 1; i += 2) {
            if (nums[i] != nums[i + 1]) {
                return nums[i];
            }
        }
        return nums[nums.length - 1];
    }
}

