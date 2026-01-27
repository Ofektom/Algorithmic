package pointers;

/**
 * Sort Colors - LeetCode 75 (Dutch National Flag Problem)
 * Given an array nums with n objects colored red, white, or blue, sort them in-place
 * so that objects of the same color are adjacent, with the colors in the order red, white, and blue.
 * We will use the integers 0, 1, and 2 to represent the color red, white, and blue, respectively.
 * You must solve this problem without using the library's sort function.
 */
public class SortColors {

    /**
     * Solution 1: Dutch National Flag Algorithm (Three Pointers)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithThreePointers(int[] nums) {
        int left = 0;
        int right = nums.length - 1;
        int current = 0;

        while (current <= right) {
            if (nums[current] == 0) {
                swap(nums, left, current);
                left++;
                current++;
            } else if (nums[current] == 2) {
                swap(nums, current, right);
                right--;
            } else {
                current++;
            }
        }
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    /**
     * Solution 2: Count and Fill
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithCounting(int[] nums) {
        int count0 = 0, count1 = 0, count2 = 0;

        for (int num : nums) {
            if (num == 0) count0++;
            else if (num == 1) count1++;
            else count2++;
        }

        int index = 0;
        for (int i = 0; i < count0; i++) nums[index++] = 0;
        for (int i = 0; i < count1; i++) nums[index++] = 1;
        for (int i = 0; i < count2; i++) nums[index++] = 2;
    }

    /**
     * Solution 3: Two Pass (Bubble Sort approach)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static void computeWithTwoPass(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] > nums[j]) {
                    int temp = nums[i];
                    nums[i] = nums[j];
                    nums[j] = temp;
                }
            }
        }
    }
}

