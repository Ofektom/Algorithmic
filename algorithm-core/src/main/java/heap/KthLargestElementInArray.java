package heap;

import java.util.PriorityQueue;

/**
 * Kth Largest Element in an Array - LeetCode 215
 * Given an integer array nums and an integer k, return the kth largest element in the array.
 * Note that it is the kth largest element in the sorted order, not the kth distinct element.
 * Can you solve it without sorting?
 */
public class KthLargestElementInArray {

    /**
     * Solution 1: Using Min Heap (Priority Queue)
     * Time Complexity: O(n log k)
     * Space Complexity: O(k)
     */
    public static int computeWithHeap(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }

    /**
     * Solution 2: Using Max Heap
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithMaxHeap(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        for (int num : nums) {
            maxHeap.offer(num);
        }

        for (int i = 0; i < k - 1; i++) {
            maxHeap.poll();
        }

        return maxHeap.peek();
    }

    /**
     * Solution 3: QuickSelect (Lomuto Partition)
     * Time Complexity: O(n) average, O(n²) worst case
     * Space Complexity: O(1)
     */
    public static int computeWithQuickSelect(int[] nums, int k) {
        int left = 0;
        int right = nums.length - 1;
        int targetIndex = nums.length - k;

        while (left <= right) {
            int pivotIndex = partition(nums, left, right);
            if (pivotIndex == targetIndex) {
                return nums[pivotIndex];
            } else if (pivotIndex < targetIndex) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }

        return -1;
    }

    private static int partition(int[] nums, int left, int right) {
        int pivot = nums[right];
        int i = left;

        for (int j = left; j < right; j++) {
            if (nums[j] <= pivot) {
                swap(nums, i, j);
                i++;
            }
        }
        swap(nums, i, right);
        return i;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    /**
     * Solution 4: Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static int computeWithSorting(int[] nums, int k) {
        java.util.Arrays.sort(nums);
        return nums[nums.length - k];
    }
}

