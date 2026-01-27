package search;

/**
 * Find Minimum in Rotated Sorted Array - LeetCode 153
 * Suppose an array of length n sorted in ascending order is rotated between 1
 * and n times. For example, the array nums = [0,1,2,4,5,6,7] might become:
 * [4,5,6,7,0,1,2] if it was rotated 4 times.
 * [0,1,2,4,5,6,7] if it was rotated 7 times.
 * Given the sorted rotated array nums of unique elements, return the minimum
 * element of this array.
 */
public class FindMinimumInRotatedSortedArray {

    /**
     * Solution 1: Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeWithBinarySearch(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return nums[left];
    }

    /**
     * Solution 2: Linear Search
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithLinearSearch(int[] nums) {
        int min = nums[0];
        for (int num : nums) {
            if (num < min) {
                min = num;
            }
        }
        return min;
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(log n)
     * Space Complexity: O(log n) for recursion stack
     */
    public static int computeWithRecursion(int[] nums) {
        return findMinRecursive(nums, 0, nums.length - 1);
    }

    private static int findMinRecursive(int[] nums, int left, int right) {
        if (left == right) {
            return nums[left];
        }

        int mid = left + (right - left) / 2;
        if (nums[mid] > nums[right]) {
            return findMinRecursive(nums, mid + 1, right);
        } else {
            return findMinRecursive(nums, left, mid);
        }
    }
}

