package pointers;

/**
 * Two Sum II - Input Array Is Sorted - LeetCode 167
 * Given a 1-indexed array of integers numbers that is already sorted in
 * non-decreasing order, find two numbers such that they add up to a specific
 * target number.
 * Return the indices of the two numbers (1-indexed) as an integer array.
 */
public class TwoSumII {

    /**
     * Solution 1: Using Two Pointers
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int[] computeWithTwoPointers(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[] { left + 1, right + 1 };
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[] {};
    }

    /**
     * Solution 2: Using Binary Search
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static int[] computeWithBinarySearch(int[] numbers, int target) {
        for (int i = 0; i < numbers.length; i++) {
            int complement = target - numbers[i];
            int index = binarySearch(numbers, i + 1, complement);
            if (index != -1) {
                return new int[] { i + 1, index + 1 };
            }
        }
        return new int[] {};
    }

    private static int binarySearch(int[] numbers, int left, int target) {
        int right = numbers.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (numbers[mid] == target) {
                return mid;
            } else if (numbers[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    /**
     * Solution 3: Using HashMap (Works but not optimal for sorted array)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithHashMap(int[] numbers, int target) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < numbers.length; i++) {
            int complement = target - numbers[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement) + 1, i + 1 };
            }
            map.put(numbers[i], i);
        }
        return new int[] {};
    }

    /**
     * Solution 4: Using Recursion with Two Pointers
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int[] computeWithRecursion(int[] numbers, int target) {
        int[] result = new int[2];
        if (twoSumRecursive(numbers, 0, numbers.length - 1, target, result)) {
            return result;
        }
        return new int[] {};
    }

    private static boolean twoSumRecursive(int[] numbers, int left, int right, int target, int[] result) {
        if (left >= right)
            return false;

        int sum = numbers[left] + numbers[right];
        if (sum == target) {
            result[0] = left + 1;
            result[1] = right + 1;
            return true;
        } else if (sum < target) {
            return twoSumRecursive(numbers, left + 1, right, target, result);
        } else {
            return twoSumRecursive(numbers, left, right - 1, target, result);
        }
    }
}

