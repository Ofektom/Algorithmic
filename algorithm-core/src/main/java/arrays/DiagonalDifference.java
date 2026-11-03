package arrays;

/**
 * Diagonal Difference
 * Given a square matrix, return the absolute difference between the sums of its diagonals.
 */
public class DiagonalDifference {

    /**
     * Solution 1: Single Pass
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithSinglePass(int[][] matrix) {
        int n = matrix.length;
        int primary = 0;
        int secondary = 0;
        for (int i = 0; i < n; i++) {
            primary += matrix[i][i];
            secondary += matrix[i][n - 1 - i];
        }
        return Math.abs(primary - secondary);
    }

    /**
     * Solution 2: Full Matrix Scan
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    public static int computeWithFullScan(int[][] matrix) {
        int n = matrix.length;
        int difference = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) difference += matrix[i][j];
                if (i + j == n - 1) difference -= matrix[i][j];
            }
        }
        return Math.abs(difference);
    }
}
