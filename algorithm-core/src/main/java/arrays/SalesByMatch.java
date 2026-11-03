package arrays;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Sales by Match
 * Given an array of sock colors, return how many matching pairs there are.
 */
public class SalesByMatch {

    /**
     * Solution 1: HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithHashSet(int[] socks) {
        Set<Integer> unmatched = new HashSet<>();
        int pairs = 0;
        for (int color : socks) {
            if (!unmatched.add(color)) {
                unmatched.remove(color);
                pairs++;
            }
        }
        return pairs;
    }

    /**
     * Solution 2: Sorting
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithSort(int[] socks) {
        int[] sorted = socks.clone();
        Arrays.sort(sorted);
        int pairs = 0;
        for (int i = 0; i < sorted.length - 1; i++) {
            if (sorted[i] == sorted[i + 1]) {
                pairs++;
                i++;
            }
        }
        return pairs;
    }
}
