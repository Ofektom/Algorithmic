package stack;

import java.util.*;

/**
 * Car Fleet - LeetCode 853
 * There are n cars going to the same destination on a one-lane road. The destination is target miles away.
 * You are given two integer array position and speed, both of length n, where position[i] is the position
 * of the ith car and speed[i] is the speed of the ith car (in miles per hour).
 * A car can never pass another car ahead of it, but it can catch up to it and drive bumper to bumper
 * at the same speed. The faster car will slow down to match the slower car's speed. The distance between
 * these two cars is ignored (i.e., they are assumed to have the same position).
 * A car fleet is some non-empty set of cars driving at the same position and same speed. Note that a
 * single car is also a car fleet. If a car catches up to a car fleet right at the destination point,
 * it will still be considered as one car fleet. Return the number of car fleets that will arrive at the destination.
 */
public class CarFleet {

    /**
     * Solution 1: Using Stack (Monotonic Stack)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithStack(int target, int[] position, int[] speed) {
        int n = position.length;
        double[][] cars = new double[n][2];

        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = (double)(target - position[i]) / speed[i];
        }

        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));

        Stack<Double> stack = new Stack<>();
        for (double[] car : cars) {
            double time = car[1];
            if (stack.isEmpty() || time > stack.peek()) {
                stack.push(time);
            }
        }

        return stack.size();
    }

    /**
     * Solution 2: Using Array (No Stack)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithArray(int target, int[] position, int[] speed) {
        int n = position.length;
        double[][] cars = new double[n][2];

        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = (double)(target - position[i]) / speed[i];
        }

        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));

        int fleets = 0;
        double maxTime = 0;

        for (double[] car : cars) {
            if (car[1] > maxTime) {
                maxTime = car[1];
                fleets++;
            }
        }

        return fleets;
    }

    /**
     * Solution 3: Using TreeMap (Sorted by Position)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static int computeWithTreeMap(int target, int[] position, int[] speed) {
        TreeMap<Integer, Double> map = new TreeMap<>(Collections.reverseOrder());

        for (int i = 0; i < position.length; i++) {
            double time = (double)(target - position[i]) / speed[i];
            map.put(position[i], time);
        }

        int fleets = 0;
        double maxTime = 0;

        for (double time : map.values()) {
            if (time > maxTime) {
                maxTime = time;
                fleets++;
            }
        }

        return fleets;
    }
}

