package search;

/**
 * Search a 2D Matrix - LeetCode 74
 * You are given an m x n integer matrix matrix with the following properties:
 * - Each row is sorted in non-decreasing order.
 * - The first integer of each row is greater than the last integer of the previous row.
 * Given an integer target, return true if target is in matrix or false otherwise.
 * You must write a solution in O(log(m * n)) time complexity.
 */
public class SearchA2DMatrix {

    /**
     * Solution 1: Binary Search (Treat as 1D Array)
     * Time Complexity: O(log(m * n))
     * Space Complexity: O(1)
     */
    public static boolean computeWithBinarySearch(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0) return false;

        int m = matrix.length;
        int n = matrix[0].length;
        int left = 0;
        int right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midValue = matrix[mid / n][mid % n];

            if (midValue == target) {
                return true;
            } else if (midValue < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    /**
     * Solution 2: Two Binary Searches (Row then Column)
     * Time Complexity: O(log(m) + log(n))
     * Space Complexity: O(1)
     */
    public static boolean computeWithTwoBinarySearches(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0) return false;

        int m = matrix.length;
        int n = matrix[0].length;

        // Find the row
        int top = 0, bottom = m - 1;
        while (top < bottom) {
            int mid = top + (bottom - top) / 2;
            if (matrix[mid][n - 1] < target) {
                top = mid + 1;
            } else {
                bottom = mid;
            }
        }

        // Search in the row
        int left = 0, right = n - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (matrix[top][mid] == target) {
                return true;
            } else if (matrix[top][mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    /**
     * Solution 3: Linear Search
     * Time Complexity: O(m * n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithLinearSearch(int[][] matrix, int target) {
        for (int[] row : matrix) {
            for (int value : row) {
                if (value == target) {
                    return true;
                }
            }
        }
        return false;
    }
}

