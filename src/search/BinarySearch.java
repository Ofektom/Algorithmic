package search;

/**
 * Binary Search - LeetCode 704
 * Given an array of integers nums which is sorted in ascending order, and an integer target,
 * write a function to search target in nums. If target exists, then return its index.
 * Otherwise, return -1. You must write an algorithm with O(log n) runtime complexity.
 */
public class BinarySearch {

    /**
     * Solution 1: Iterative Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeIterative(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    /**
     * Solution 2: Recursive Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(log n)
     */
    public static int computeRecursive(int[] nums, int target) {
        return binarySearchRecursive(nums, target, 0, nums.length - 1);
    }

    private static int binarySearchRecursive(int[] nums, int target, int left, int right) {
        if (left > right) return -1;

        int mid = left + (right - left) / 2;
        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            return binarySearchRecursive(nums, target, mid + 1, right);
        } else {
            return binarySearchRecursive(nums, target, left, mid - 1);
        }
    }

    /**
     * Solution 3: Using Arrays.binarySearch
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeWithArrays(int[] nums, int target) {
        int index = java.util.Arrays.binarySearch(nums, target);
        return index >= 0 ? index : -1;
    }
}
