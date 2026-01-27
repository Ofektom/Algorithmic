package pointers;

/**
 * Move Zeroes - LeetCode 283
 * Given an integer array nums, move all 0's to the end of it while maintaining the
 * relative order of the non-zero elements. Note that you must do this in-place
 * without making a copy of the array.
 */
public class MoveZeroes {

    /**
     * Solution 1: Two Pointers
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithTwoPointers(int[] nums) {
        int writeIndex = 0;
        for (int readIndex = 0; readIndex < nums.length; readIndex++) {
            if (nums[readIndex] != 0) {
                nums[writeIndex] = nums[readIndex];
                writeIndex++;
            }
        }
        while (writeIndex < nums.length) {
            nums[writeIndex] = 0;
            writeIndex++;
        }
    }

    /**
     * Solution 2: Swapping (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithSwapping(int[] nums) {
        int lastNonZeroIndex = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[lastNonZeroIndex];
                nums[lastNonZeroIndex] = nums[i];
                nums[i] = temp;
                lastNonZeroIndex++;
            }
        }
    }

    /**
     * Solution 3: Using ArrayList
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static void computeWithArrayList(int[] nums) {
        java.util.List<Integer> nonZeros = new java.util.ArrayList<>();
        for (int num : nums) {
            if (num != 0) {
                nonZeros.add(num);
            }
        }
        for (int i = 0; i < nonZeros.size(); i++) {
            nums[i] = nonZeros.get(i);
        }
        for (int i = nonZeros.size(); i < nums.length; i++) {
            nums[i] = 0;
        }
    }
}

