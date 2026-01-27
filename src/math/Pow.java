package math;

/**
 * Pow(x, n) - LeetCode 50
 * Implement pow(x, n), which calculates x raised to the power n (i.e., x^n).
 */
public class Pow {

    /**
     * Solution 1: Fast Power (Recursive)
     * Time Complexity: O(log n)
     * Space Complexity: O(log n)
     */
    public static double computeRecursive(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }
        return fastPower(x, N);
    }

    private static double fastPower(double x, long n) {
        if (n == 0) return 1.0;
        double half = fastPower(x, n / 2);
        if (n % 2 == 0) {
            return half * half;
        } else {
            return half * half * x;
        }
    }

    /**
     * Solution 2: Iterative Fast Power
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static double computeIterative(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        double currentProduct = x;

        while (N > 0) {
            if (N % 2 == 1) {
                result *= currentProduct;
            }
            currentProduct *= currentProduct;
            N /= 2;
        }

        return result;
    }

    /**
     * Solution 3: Simple Recursive (Brute Force)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static double computeBruteForce(double x, int n) {
        if (n == 0) return 1.0;
        if (n < 0) {
            x = 1 / x;
            n = -n;
        }
        return x * computeBruteForce(x, n - 1);
    }
}

