package dp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Coin Change - LeetCode 322
 * You are given an integer array coins representing coins of different denominations
 * and an integer amount representing a total amount of money.
 * Return the fewest number of coins that you need to make up that amount.
 * If that amount of money cannot be made up by any combination of the coins, return -1.
 * You may assume that you have an infinite number of each kind of coin.
 */
public class CoinChange {

    /**
     * Solution 1: Dynamic Programming (Bottom-Up)
     * Time Complexity: O(amount * coins.length)
     * Space Complexity: O(amount)
     */
    public static int computeWithDP(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        
        return dp[amount] > amount ? -1 : dp[amount];
    }

    /**
     * Solution 2: Recursion with Memoization (Top-Down)
     * Time Complexity: O(amount * coins.length)
     * Space Complexity: O(amount)
     */
    public static int computeWithRecursion(int[] coins, int amount) {
        Map<Integer, Integer> memo = new HashMap<>();
        int result = coinChangeRecursive(coins, amount, memo);
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    private static int coinChangeRecursive(int[] coins, int amount, Map<Integer, Integer> memo) {
        if (amount == 0) return 0;
        if (amount < 0) return Integer.MAX_VALUE;
        if (memo.containsKey(amount)) return memo.get(amount);
        
        int minCoins = Integer.MAX_VALUE;
        for (int coin : coins) {
            int result = coinChangeRecursive(coins, amount - coin, memo);
            if (result != Integer.MAX_VALUE) {
                minCoins = Math.min(minCoins, result + 1);
            }
        }
        
        memo.put(amount, minCoins);
        return minCoins;
    }

    /**
     * Solution 3: Pure Recursion
     * Time Complexity: O(coins.length^amount)
     * Space Complexity: O(amount)
     */
    public static int computeWithPureRecursion(int[] coins, int amount) {
        if (amount == 0) return 0;
        if (amount < 0) return -1;
        
        int minCoins = Integer.MAX_VALUE;
        for (int coin : coins) {
            int result = computeWithPureRecursion(coins, amount - coin);
            if (result != -1) {
                minCoins = Math.min(minCoins, result + 1);
            }
        }
        
        return minCoins == Integer.MAX_VALUE ? -1 : minCoins;
    }
}

