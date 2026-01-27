package window;

/**
 * Best Time to Buy and Sell Stock - LeetCode 121
 * You are given an array prices where prices[i] is the price of a given stock
 * on the ith day.
 * You want to maximize your profit by choosing a single day to buy one stock
 * and choosing a different day in the future to sell that stock.
 * Return the maximum profit you can achieve from this transaction. If you
 * cannot achieve any profit, return 0.
 */
public class BestTimeToBuySellStock {

    /**
     * Solution 1: One Pass (Sliding Window Concept)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithOnePass(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else {
                maxProfit = Math.max(maxProfit, price - minPrice);
            }
        }
        return maxProfit;
    }

    /**
     * Solution 2: Using Two Pointers (Sliding Window)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithTwoPointers(int[] prices) {
        int left = 0;
        int maxProfit = 0;

        for (int right = 1; right < prices.length; right++) {
            if (prices[right] < prices[left]) {
                left = right;
            } else {
                maxProfit = Math.max(maxProfit, prices[right] - prices[left]);
            }
        }
        return maxProfit;
    }

    /**
     * Solution 3: Brute Force
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int computeWithBruteForce(int[] prices) {
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                maxProfit = Math.max(maxProfit, prices[j] - prices[i]);
            }
        }
        return maxProfit;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(int[] prices) {
        return maxProfitRecursive(prices, 0, Integer.MAX_VALUE, 0);
    }

    private static int maxProfitRecursive(int[] prices, int index, int minPrice, int maxProfit) {
        if (index >= prices.length)
            return maxProfit;

        int currentPrice = prices[index];
        minPrice = Math.min(minPrice, currentPrice);
        maxProfit = Math.max(maxProfit, currentPrice - minPrice);

        return maxProfitRecursive(prices, index + 1, minPrice, maxProfit);
    }
}

