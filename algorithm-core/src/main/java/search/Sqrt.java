package search;

/**
 * Sqrt(x) - LeetCode 69
 * Given a non-negative integer x, return the square root of x rounded down to the
 * nearest integer. The returned integer should be non-negative as well.
 * You must not use any built-in exponent function or operator.
 */
public class Sqrt {

    /**
     * Solution 1: Binary Search
     * Time Complexity: O(log x)
     * Space Complexity: O(1)
     */
    public static int computeWithBinarySearch(int x) {
        if (x < 2) return x;

        int left = 2;
        int right = x / 2;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            long square = (long) mid * mid;

            if (square == x) {
                return mid;
            } else if (square < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }

    /**
     * Solution 2: Newton's Method
     * Time Complexity: O(log log x)
     * Space Complexity: O(1)
     */
    public static int computeWithNewton(int x) {
        if (x < 2) return x;

        long result = x;
        while (result * result > x) {
            result = (result + x / result) / 2;
        }

        return (int) result;
    }

    /**
     * Solution 3: Using Built-in Math
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public static int computeWithMath(int x) {
        return (int) Math.sqrt(x);
    }

    /**
     * Solution 4: Linear Search
     * Time Complexity: O(√x)
     * Space Complexity: O(1)
     */
    public static int computeWithLinearSearch(int x) {
        if (x < 2) return x;

        for (int i = 2; i <= x / 2; i++) {
            long square = (long) i * i;
            if (square == x) {
                return i;
            } else if (square > x) {
                return i - 1;
            }
        }

        return x / 2;
    }
}

