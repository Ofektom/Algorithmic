package intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Merge Intervals - LeetCode 56
 * Given an array of intervals where intervals[i] = [starti, endi],
 * merge all overlapping intervals, and return an array of the non-overlapping intervals
 * that cover all the intervals in the input.
 */
public class MergeIntervals {

    /**
     * Solution 1: Sort and Merge
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int[][] computeWithSorting(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        List<int[]> merged = new ArrayList<>();
        int[] currentInterval = intervals[0];
        merged.add(currentInterval);

        for (int[] interval : intervals) {
            int currentEnd = currentInterval[1];
            int nextStart = interval[0];
            int nextEnd = interval[1];

            if (currentEnd >= nextStart) {
                currentInterval[1] = Math.max(currentEnd, nextEnd);
            } else {
                currentInterval = interval;
                merged.add(currentInterval);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }

    /**
     * Solution 2: Using ArrayList with Comparator
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int[][] computeWithArrayList(int[][] intervals) {
        if (intervals.length == 0) return new int[0][];

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> result = new ArrayList<>();
        result.add(new int[]{intervals[0][0], intervals[0][1]});

        for (int i = 1; i < intervals.length; i++) {
            int[] last = result.get(result.size() - 1);
            int[] current = intervals[i];

            if (current[0] <= last[1]) {
                last[1] = Math.max(last[1], current[1]);
            } else {
                result.add(new int[]{current[0], current[1]});
            }
        }

        return result.toArray(new int[result.size()][]);
    }

    /**
     * Solution 3: Recursive Approach
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int[][] computeRecursive(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        List<int[]> merged = new ArrayList<>();
        mergeIntervals(intervals, 0, merged);

        return merged.toArray(new int[merged.size()][]);
    }

    private static void mergeIntervals(int[][] intervals, int index, List<int[]> merged) {
        if (index >= intervals.length) return;

        int[] current = intervals[index];

        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < current[0]) {
            merged.add(current);
        } else {
            int[] last = merged.get(merged.size() - 1);
            last[1] = Math.max(last[1], current[1]);
        }

        mergeIntervals(intervals, index + 1, merged);
    }
}

