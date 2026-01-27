package search;

/**
 * First Bad Version - LeetCode 278
 * You are a product manager and currently leading a team to develop a new product.
 * Unfortunately, the latest version of your product fails the quality check.
 * Since each version is developed based on the previous version, all the versions
 * after a bad version are also bad. Suppose you have n versions [1, 2, ..., n] and
 * you want to find out the first bad one, which causes all the following ones to be bad.
 * You are given an API bool isBadVersion(version) which returns whether version is bad.
 * Implement a function to find the first bad version. You should minimize the number
 * of calls to the API.
 */
public class FirstBadVersion {

    // Mock API for testing
    private static int badVersion = 4;

    public static void setBadVersion(int version) {
        badVersion = version;
    }

    public static boolean isBadVersion(int version) {
        return version >= badVersion;
    }

    /**
     * Solution 1: Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public static int computeWithBinarySearch(int n) {
        int left = 1;
        int right = n;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (isBadVersion(mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    /**
     * Solution 2: Recursive Binary Search
     * Time Complexity: O(log n)
     * Space Complexity: O(log n)
     */
    public static int computeRecursive(int n) {
        return findFirstBadVersion(1, n);
    }

    private static int findFirstBadVersion(int left, int right) {
        if (left == right) {
            return left;
        }

        int mid = left + (right - left) / 2;
        if (isBadVersion(mid)) {
            return findFirstBadVersion(left, mid);
        } else {
            return findFirstBadVersion(mid + 1, right);
        }
    }

    /**
     * Solution 3: Linear Search
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithLinearSearch(int n) {
        for (int i = 1; i <= n; i++) {
            if (isBadVersion(i)) {
                return i;
            }
        }
        return -1;
    }
}

