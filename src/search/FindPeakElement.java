package search;

/**
 * Find Peak Element - LeetCode 162
 * A peak element is an element that is strictly greater than its neighbors.
 * Given a 0-indexed integer array nums, find a peak element, and return its index.
 * If the array contains multiple peaks, return the index to any of the peaks.
 * You may imagine that nums[-1] = nums[n] = -∞.
 * You must write an algorithm that runs in O(log n) time.
 */
public class FindPeakElement {

    /**
     * Solution 1: Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeWithBinarySearch(int[] nums) {
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    /**
     * Solution 2: Linear Search
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithLinearSearch(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                return i;
            }
        }
        return nums.length - 1;
    }

    /**
     * Solution 3: Recursive Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(log n)
     */
    public static int computeRecursive(int[] nums) {
        return findPeakRecursive(nums, 0, nums.length - 1);
    }

    private static int findPeakRecursive(int[] nums, int left, int right) {
        if (left == right) return left;
        
        int mid = left + (right - left) / 2;
        if (nums[mid] > nums[mid + 1]) {
            return findPeakRecursive(nums, left, mid);
        } else {
            return findPeakRecursive(nums, mid + 1, right);
        }
    }
}

