package trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Path Sum - LeetCode 112
 * Given the root of a binary tree and an integer targetSum, return true if the tree
 * has a root-to-leaf path such that adding up all the values along the path equals targetSum.
 * A leaf is a node with no children.
 */
public class PathSum {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Solution 1: Recursive DFS
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height
     */
    public static boolean computeRecursive(TreeNode root, int targetSum) {
        if (root == null) return false;
        if (root.left == null && root.right == null) {
            return root.val == targetSum;
        }
        return computeRecursive(root.left, targetSum - root.val) ||
               computeRecursive(root.right, targetSum - root.val);
    }

    /**
     * Solution 2: Iterative DFS with Stack
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static boolean computeWithStack(TreeNode root, int targetSum) {
        if (root == null) return false;

        java.util.Stack<Pair> stack = new java.util.Stack<>();
        stack.push(new Pair(root, targetSum - root.val));

        while (!stack.isEmpty()) {
            Pair pair = stack.pop();
            TreeNode node = pair.node;
            int remainingSum = pair.sum;

            if (node.left == null && node.right == null && remainingSum == 0) {
                return true;
            }

            if (node.right != null) {
                stack.push(new Pair(node.right, remainingSum - node.right.val));
            }
            if (node.left != null) {
                stack.push(new Pair(node.left, remainingSum - node.left.val));
            }
        }
        return false;
    }

    private static class Pair {
        TreeNode node;
        int sum;

        Pair(TreeNode node, int sum) {
            this.node = node;
            this.sum = sum;
        }
    }

    /**
     * Solution 3: BFS with Queue
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithBFS(TreeNode root, int targetSum) {
        if (root == null) return false;

        java.util.Queue<Pair> queue = new java.util.LinkedList<>();
        queue.offer(new Pair(root, targetSum - root.val));

        while (!queue.isEmpty()) {
            Pair pair = queue.poll();
            TreeNode node = pair.node;
            int remainingSum = pair.sum;

            if (node.left == null && node.right == null && remainingSum == 0) {
                return true;
            }

            if (node.left != null) {
                queue.offer(new Pair(node.left, remainingSum - node.left.val));
            }
            if (node.right != null) {
                queue.offer(new Pair(node.right, remainingSum - node.right.val));
            }
        }
        return false;
    }
}

