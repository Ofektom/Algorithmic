package arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * Plus One - LeetCode 66
 * You are given a large integer represented as an integer array digits, where
 * each digits[i] is the ith digit of the integer. The digits are ordered from
 * most significant to least significant in left-to-right order. The large
 * integer does not contain any leading zeros.
 * Increment the large integer by one and return the resulting array of digits.
 */
public class PlusOne {

    /**
     * Solution 1: Iterative from Right to Left
     * Time Complexity: O(n)
     * Space Complexity: O(1) excluding output
     */
    public static int[] computeIterative(int[] digits) {
        int n = digits.length;

        for (int i = n - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits;
            }
            digits[i] = 0;
        }

        // If all digits were 9
        int[] result = new int[n + 1];
        result[0] = 1;
        return result;
    }

    /**
     * Solution 2: Using ArrayList
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithArrayList(int[] digits) {
        List<Integer> list = new ArrayList<>();
        int carry = 1;

        for (int i = digits.length - 1; i >= 0; i--) {
            int sum = digits[i] + carry;
            list.add(0, sum % 10);
            carry = sum / 10;
        }

        if (carry > 0) {
            list.add(0, carry);
        }

        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int[] computeWithRecursion(int[] digits) {
        int[] result = addOneRecursive(digits, digits.length - 1, 1);
        return result;
    }

    private static int[] addOneRecursive(int[] digits, int index, int carry) {
        if (index < 0) {
            if (carry > 0) {
                int[] newDigits = new int[digits.length + 1];
                newDigits[0] = carry;
                System.arraycopy(digits, 0, newDigits, 1, digits.length);
                return newDigits;
            }
            return digits;
        }

        int sum = digits[index] + carry;
        digits[index] = sum % 10;
        carry = sum / 10;

        return addOneRecursive(digits, index - 1, carry);
    }

    /**
     * Solution 4: Convert to Long (may overflow for large numbers)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithLong(int[] digits) {
        long num = 0;
        for (int digit : digits) {
            num = num * 10 + digit;
        }
        num++;

        List<Integer> list = new ArrayList<>();
        while (num > 0) {
            list.add(0, (int) (num % 10));
            num /= 10;
        }

        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}

