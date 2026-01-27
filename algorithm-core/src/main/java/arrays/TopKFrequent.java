package arrays;

import java.util.*;

/**
 * Top K Frequent Elements - LeetCode 347
 * Given an integer array nums and an integer k, return the k most frequent
 * elements. You may return the answer in any order.
 */
public class TopKFrequent {

    /**
     * Solution 1: Using HashMap and PriorityQueue (Min Heap)
     * Time Complexity: O(n log k)
     * Space Complexity: O(n)
     */
    public static int[] computeWithHeap(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(
                (a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            pq.offer(entry);
            if (pq.size() > k) {
                pq.poll();
            }
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = pq.poll().getKey();
        }
        return result;
    }

    /**
     * Solution 2: Using HashMap and Bucket Sort
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithBucketSort(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        @SuppressWarnings("unchecked")
        List<Integer>[] buckets = new List[nums.length + 1];
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int frequency = entry.getValue();
            if (buckets[frequency] == null) {
                buckets[frequency] = new ArrayList<>();
            }
            buckets[frequency].add(entry.getKey());
        }

        int[] result = new int[k];
        int idx = 0;
        for (int i = buckets.length - 1; i >= 0 && idx < k; i--) {
            if (buckets[i] != null) {
                for (int num : buckets[i]) {
                    result[idx++] = num;
                    if (idx == k)
                        break;
                }
            }
        }
        return result;
    }

    /**
     * Solution 3: Using HashMap and Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithSorting(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = entries.get(i).getKey();
        }
        return result;
    }

    /**
     * Solution 4: Using Recursion with HashMap
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithRecursion(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freq.entrySet());
        sortByFrequency(entries, 0, entries.size() - 1);

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = entries.get(i).getKey();
        }
        return result;
    }

    private static void sortByFrequency(List<Map.Entry<Integer, Integer>> entries, int low, int high) {
        if (low < high) {
            int pi = partition(entries, low, high);
            sortByFrequency(entries, low, pi - 1);
            sortByFrequency(entries, pi + 1, high);
        }
    }

    private static int partition(List<Map.Entry<Integer, Integer>> entries, int low, int high) {
        int pivot = entries.get(high).getValue();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (entries.get(j).getValue() >= pivot) {
                i++;
                Collections.swap(entries, i, j);
            }
        }
        Collections.swap(entries, i + 1, high);
        return i + 1;
    }
}

