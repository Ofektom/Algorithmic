package arrays;

import java.util.HashSet;
import java.util.Set;

/**
 * Longest Consecutive Sequence - LeetCode 128
 * Given an unsorted array of integers nums, return the length of the longest
 * consecutive elements sequence.
 * You must write an algorithm that runs in O(n) time.
 */
public class LongestConsecutive {

    /**
     * Solution 1: Using HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithHashSet(int[] nums) {
        if (nums.length == 0)
            return 0;

        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        int longest = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                int current = num;
                int length = 0;
                while (set.contains(current)) {
                    length++;
                    current++;
                }
                longest = Math.max(longest, length);
            }
        }
        return longest;
    }

    /**
     * Solution 2: Using HashSet with Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithRecursion(int[] nums) {
        if (nums.length == 0)
            return 0;

        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        int longest = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                longest = Math.max(longest, findSequenceLength(set, num));
            }
        }
        return longest;
    }

    private static int findSequenceLength(Set<Integer> set, int num) {
        if (!set.contains(num))
            return 0;
        return 1 + findSequenceLength(set, num + 1);
    }

    /**
     * Solution 3: Using Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static int computeWithSorting(int[] nums) {
        if (nums.length == 0)
            return 0;

        java.util.Arrays.sort(nums);
        int longest = 1;
        int current = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1] + 1) {
                current++;
            } else if (nums[i] != nums[i - 1]) {
                longest = Math.max(longest, current);
                current = 1;
            }
        }
        return Math.max(longest, current);
    }

    /**
     * Solution 4: Brute Force (Linear Search for each element)
     * Time Complexity: O(n³)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] nums) {
        if (nums.length == 0)
            return 0;

        int longest = 0;
        for (int num : nums) {
            int current = num;
            int length = 0;
            while (contains(nums, current)) {
                length++;
                current++;
            }
            longest = Math.max(longest, length);
        }
        return longest;
    }

    private static boolean contains(int[] nums, int target) {
        for (int num : nums) {
            if (num == target)
                return true;
        }
        return false;
    }
}

