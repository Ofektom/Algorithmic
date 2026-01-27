package pointers;

/**
 * Remove Duplicates from Sorted Array - LeetCode 26
 * Given an integer array nums sorted in non-decreasing order, remove the duplicates
 * in-place such that each unique element appears only once. The relative order of the
 * elements should be kept the same. Then return the number of unique elements in nums.
 * Consider the number of unique elements of nums to be k, to get accepted, you need to
 * do the following things:
 * - Change the array nums such that the first k elements of nums contain the unique elements
 *   in the order they were present in nums initially. The remaining elements of nums are not
 *   important as well as the size of nums.
 * - Return k.
 */
public class RemoveDuplicatesFromSortedArray {

    /**
     * Solution 1: Two Pointers
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithTwoPointers(int[] nums) {
        if (nums.length == 0) return 0;

        int writeIndex = 1;
        for (int readIndex = 1; readIndex < nums.length; readIndex++) {
            if (nums[readIndex] != nums[readIndex - 1]) {
                nums[writeIndex] = nums[readIndex];
                writeIndex++;
            }
        }
        return writeIndex;
    }

    /**
     * Solution 2: Using ArrayList
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithArrayList(int[] nums) {
        if (nums.length == 0) return 0;

        java.util.List<Integer> result = new java.util.ArrayList<>();
        result.add(nums[0]);

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                result.add(nums[i]);
            }
        }

        for (int i = 0; i < result.size(); i++) {
            nums[i] = result.get(i);
        }

        return result.size();
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] nums) {
        if (nums.length == 0) return 0;

        int length = nums.length;
        for (int i = 1; i < length; i++) {
            if (nums[i] == nums[i - 1]) {
                for (int j = i; j < length - 1; j++) {
                    nums[j] = nums[j + 1];
                }
                length--;
                i--;
            }
        }
        return length;
    }
}

