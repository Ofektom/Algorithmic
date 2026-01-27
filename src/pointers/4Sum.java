package pointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 4Sum - LeetCode 18
 * Given an array nums of n integers, return an array of all the unique quadruplets
 * [nums[a], nums[b], nums[c], nums[d]] such that:
 * 0 <= a, b, c, d < n
 * a, b, c, and d are distinct.
 * nums[a] + nums[b] + nums[c] + nums[d] == target
 * You may return the answer in any order.
 */
public class FourSum {

    /**
     * Solution 1: Two Pointers (Sorting)
     * Time Complexity: O(n³)
     * Space Complexity: O(1) excluding output
     */
    public static List<List<Integer>> computeWithTwoPointers(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            for (int j = i + 1; j < n - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;

                int left = j + 1;
                int right = n - 1;

                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];

                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                        while (left < right && nums[left] == nums[left + 1]) left++;
                        while (left < right && nums[right] == nums[right - 1]) right--;

                        left++;
                        right--;
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Solution 2: Using HashSet
     * Time Complexity: O(n³)
     * Space Complexity: O(n)
     */
    public static List<List<Integer>> computeWithHashSet(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            for (int j = i + 1; j < n - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;

                java.util.Set<Integer> seen = new java.util.HashSet<>();
                for (int k = j + 1; k < n; k++) {
                    int complement = target - nums[i] - nums[j] - nums[k];
                    if (seen.contains(complement)) {
                        result.add(Arrays.asList(nums[i], nums[j], complement, nums[k]));
                        while (k + 1 < n && nums[k] == nums[k + 1]) k++;
                    }
                    seen.add(nums[k]);
                }
            }
        }

        return result;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n⁴)
     * Space Complexity: O(1)
     */
    public static List<List<Integer>> computeWithBruteForce(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 1; j < n - 2; j++) {
                for (int k = j + 1; k < n - 1; k++) {
                    for (int l = k + 1; l < n; l++) {
                        if (nums[i] + nums[j] + nums[k] + nums[l] == target) {
                            List<Integer> quad = Arrays.asList(nums[i], nums[j], nums[k], nums[l]);
                            if (!result.contains(quad)) {
                                result.add(quad);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}

