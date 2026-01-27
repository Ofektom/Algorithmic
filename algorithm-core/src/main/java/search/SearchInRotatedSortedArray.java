package search;

/**
 * Search in Rotated Sorted Array - LeetCode 33
 * There is an integer array nums sorted in ascending order (with distinct
 * values).
 * Prior to being passed to your function, nums is possibly rotated at an
 * unknown pivot index k (1 <= k < nums.length) such that the resulting array is
 * [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]].
 * Given the array nums after the rotation and an integer target, return the
 * index of target if it is in nums, or -1 if it is not in nums.
 */
public class SearchInRotatedSortedArray {

    /**
     * Solution 1: Binary Search with Rotation Detection
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeWithBinarySearch(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }

            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * Solution 2: Find Pivot then Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeWithPivotSearch(int[] nums, int target) {
        int pivot = findPivot(nums);
        if (pivot == -1) {
            return binarySearch(nums, 0, nums.length - 1, target);
        }

        if (nums[pivot] == target) {
            return pivot;
        }
        if (target >= nums[0]) {
            return binarySearch(nums, 0, pivot - 1, target);
        }
        return binarySearch(nums, pivot + 1, nums.length - 1, target);
    }

    private static int findPivot(int[] nums) {
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
        return left;
    }

    private static int binarySearch(int[] nums, int left, int right, int target) {
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
     * Solution 3: Linear Search
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithLinearSearch(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(log n)
     * Space Complexity: O(log n) for recursion stack
     */
    public static int computeWithRecursion(int[] nums, int target) {
        return searchRecursive(nums, target, 0, nums.length - 1);
    }

    private static int searchRecursive(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;
        if (nums[mid] == target) {
            return mid;
        }

        if (nums[left] <= nums[mid]) {
            if (target >= nums[left] && target < nums[mid]) {
                return searchRecursive(nums, target, left, mid - 1);
            } else {
                return searchRecursive(nums, target, mid + 1, right);
            }
        } else {
            if (target > nums[mid] && target <= nums[right]) {
                return searchRecursive(nums, target, mid + 1, right);
            } else {
                return searchRecursive(nums, target, left, mid - 1);
            }
        }
    }
}

