package bitmanip;

/**
 * Number of 1 Bits - LeetCode 191
 * Write a function that takes the binary representation of an unsigned integer
 * and returns the number of '1' bits it has (also known as the Hamming weight).
 */
public class NumberOf1Bits {

    /**
     * Solution 1: Using Built-in Method
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public static int computeWithBuiltIn(int n) {
        return Integer.bitCount(n);
    }

    /**
     * Solution 2: Loop and Check Each Bit
     * Time Complexity: O(32) = O(1)
     * Space Complexity: O(1)
     */
    public static int computeWithLoop(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Solution 3: Brian Kernighan's Algorithm (Turn off rightmost 1-bit)
     * Time Complexity: O(number of 1 bits) = O(1) average
     * Space Complexity: O(1)
     */
    public static int computeWithKernighan(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1); // Turn off the rightmost 1-bit
            count++;
        }
        return count;
    }

    /**
     * Solution 4: Shift Right and Check
     * Time Complexity: O(32) = O(1)
     * Space Complexity: O(1)
     */
    public static int computeWithShift(int n) {
        int count = 0;
        while (n != 0) {
            count += (n & 1);
            n >>>= 1; // Unsigned right shift
        }
        return count;
    }

    /**
     * Solution 5: Recursive
     * Time Complexity: O(number of 1 bits)
     * Space Complexity: O(number of 1 bits)
     */
    public static int computeRecursive(int n) {
        if (n == 0) return 0;
        return (n & 1) + computeRecursive(n >>> 1);
    }
}

