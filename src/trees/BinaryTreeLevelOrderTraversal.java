package trees;

import java.util.*;

/**
 * Binary Tree Level Order Traversal - LeetCode 102
 * Given the root of a binary tree, return the level order traversal of its nodes' values.
 * (i.e., from left to right, level by level).
 */
public class BinaryTreeLevelOrderTraversal {

    /**
     * Definition for a binary tree node
     */
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
     * Solution 1: BFS with Queue
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static List<List<Integer>> computeWithBFS(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(level);
        }

        return result;
    }

    /**
     * Solution 2: DFS with Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height
     */
    public static List<List<Integer>> computeWithRecursion(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        levelOrderRecursive(root, 0, result);
        return result;
    }

    private static void levelOrderRecursive(TreeNode node, int level, List<List<Integer>> result) {
        if (node == null) return;

        if (result.size() <= level) {
            result.add(new ArrayList<>());
        }

        result.get(level).add(node.val);
        levelOrderRecursive(node.left, level + 1, result);
        levelOrderRecursive(node.right, level + 1, result);
    }

    /**
     * Solution 3: DFS with Stack (Iterative)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static List<List<Integer>> computeWithStack(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Stack<TreeNode> stack = new Stack<>();
        Stack<Integer> levelStack = new Stack<>();
        stack.push(root);
        levelStack.push(0);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            int level = levelStack.pop();

            if (result.size() <= level) {
                result.add(new ArrayList<>());
            }

            result.get(level).add(node.val);

            if (node.right != null) {
                stack.push(node.right);
                levelStack.push(level + 1);
            }
            if (node.left != null) {
                stack.push(node.left);
                levelStack.push(level + 1);
            }
        }

        return result;
    }
}

