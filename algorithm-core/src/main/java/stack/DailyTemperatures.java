package stack;

import java.util.Stack;

/**
 * Daily Temperatures - LeetCode 739
 * Given an array of integers temperatures represents the daily temperatures,
 * return an array answer such that answer[i] is the number of days you have to
 * wait after the ith day to get a warmer temperature. If there is no future day
 * for which this is possible, keep answer[i] == 0 instead.
 */
public class DailyTemperatures {

    /**
     * Solution 1: Using Monotonic Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithStack(int[] temperatures) {
        int[] result = new int[temperatures.length];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < temperatures.length; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int index = stack.pop();
                result[index] = i - index;
            }
            stack.push(i);
        }
        return result;
    }

    /**
     * Solution 2: Using Array as Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] computeWithArray(int[] temperatures) {
        int[] result = new int[temperatures.length];
        int[] stack = new int[temperatures.length];
        int top = -1;

        for (int i = 0; i < temperatures.length; i++) {
            while (top >= 0 && temperatures[i] > temperatures[stack[top]]) {
                int index = stack[top--];
                result[index] = i - index;
            }
            stack[++top] = i;
        }
        return result;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1) excluding output
     */
    public static int[] computeWithBruteForce(int[] temperatures) {
        int[] result = new int[temperatures.length];
        for (int i = 0; i < temperatures.length; i++) {
            for (int j = i + 1; j < temperatures.length; j++) {
                if (temperatures[j] > temperatures[i]) {
                    result[i] = j - i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(n²)
     * Space Complexity: O(n) for recursion stack
     */
    public static int[] computeWithRecursion(int[] temperatures) {
        int[] result = new int[temperatures.length];
        findWarmerDays(temperatures, result, 0);
        return result;
    }

    private static void findWarmerDays(int[] temperatures, int[] result, int index) {
        if (index >= temperatures.length)
            return;

        findWarmerDay(temperatures, result, index, index + 1);
        findWarmerDays(temperatures, result, index + 1);
    }

    private static void findWarmerDay(int[] temperatures, int[] result, int current, int next) {
        if (next >= temperatures.length)
            return;

        if (temperatures[next] > temperatures[current]) {
            result[current] = next - current;
            return;
        }

        findWarmerDay(temperatures, result, current, next + 1);
    }
}

