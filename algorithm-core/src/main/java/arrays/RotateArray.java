package arrays;

/**
 * Rotate Array - LeetCode 189
 * Given an integer array nums, rotate the array to the right by k steps, where k is non-negative.
 */
public class RotateArray {

    /**
     * Solution 1: Using Extra Array
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static void computeWithExtraArray(int[] nums, int k) {
        int n = nums.length;
        k = k % n;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            result[(i + k) % n] = nums[i];
        }

        System.arraycopy(result, 0, nums, 0, n);
    }

    /**
     * Solution 2: Reverse Array (Three Reverses)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithReverse(int[] nums, int k) {
        int n = nums.length;
        k = k % n;

        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }

    /**
     * Solution 3: Using ArrayList
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static void computeWithArrayList(int[] nums, int k) {
        int n = nums.length;
        k = k % n;
        java.util.List<Integer> list = new java.util.ArrayList<>();

        for (int num : nums) {
            list.add(num);
        }

        for (int i = 0; i < k; i++) {
            list.add(0, list.remove(list.size() - 1));
        }

        for (int i = 0; i < n; i++) {
            nums[i] = list.get(i);
        }
    }

    /**
     * Solution 4: Cyclic Replacements
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void computeWithCyclic(int[] nums, int k) {
        int n = nums.length;
        k = k % n;
        int count = 0;

        for (int start = 0; count < n; start++) {
            int current = start;
            int prev = nums[start];

            do {
                int next = (current + k) % n;
                int temp = nums[next];
                nums[next] = prev;
                prev = temp;
                current = next;
                count++;
            } while (start != current);
        }
    }
}

