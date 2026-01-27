package dp;

import java.util.*;

/**
 * Word Break - LeetCode 139
 * Given a string s and a dictionary of strings wordDict, return true if s can be
 * segmented into a space-separated sequence of one or more dictionary words.
 * Note that the same word in the dictionary may be reused multiple times in the segmentation.
 */
public class WordBreak {

    /**
     * Solution 1: Dynamic Programming (Bottom-Up)
     * Time Complexity: O(n * m * k) where n is s.length(), m is wordDict.size(), k is average word length
     * Space Complexity: O(n)
     */
    public static boolean computeWithDP(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[s.length()];
    }

    /**
     * Solution 2: Recursion with Memoization (Top-Down)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static boolean computeWithRecursion(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<String, Boolean> memo = new HashMap<>();
        return wordBreakRecursive(s, wordSet, memo);
    }

    private static boolean wordBreakRecursive(String s, Set<String> wordSet, Map<String, Boolean> memo) {
        if (s.isEmpty()) return true;
        if (memo.containsKey(s)) return memo.get(s);

        for (int i = 1; i <= s.length(); i++) {
            String prefix = s.substring(0, i);
            if (wordSet.contains(prefix) && wordBreakRecursive(s.substring(i), wordSet, memo)) {
                memo.put(s, true);
                return true;
            }
        }

        memo.put(s, false);
        return false;
    }

    /**
     * Solution 3: BFS (Breadth-First Search)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static boolean computeWithBFS(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[s.length()];

        queue.offer(0);

        while (!queue.isEmpty()) {
            int start = queue.poll();

            if (start == s.length()) {
                return true;
            }

            if (visited[start]) {
                continue;
            }

            visited[start] = true;

            for (int end = start + 1; end <= s.length(); end++) {
                if (wordSet.contains(s.substring(start, end))) {
                    queue.offer(end);
                }
            }
        }

        return false;
    }
}

