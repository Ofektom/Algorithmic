package trees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Binary Tree Right Side View - LeetCode 199
 * Given the root of a binary tree, imagine yourself standing on the right side of it,
 * return the values of the nodes you can see ordered from top to bottom.
 */
public class BinaryTreeRightSideView {

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
     * Solution 1: BFS (Level Order Traversal)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static List<Integer> computeWithBFS(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (i == levelSize - 1) {
                    result.add(node.val);
                }
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return result;
    }

    /**
     * Solution 2: DFS (Right First)
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height
     */
    public static List<Integer> computeWithDFS(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        rightSideView(root, result, 0);
        return result;
    }

    private static void rightSideView(TreeNode node, List<Integer> result, int depth) {
        if (node == null) return;
        if (depth == result.size()) {
            result.add(node.val);
        }
        rightSideView(node.right, result, depth + 1);
        rightSideView(node.left, result, depth + 1);
    }

    /**
     * Solution 3: DFS Recursive
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static List<Integer> computeRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        traverse(root, result, 0);
        return result;
    }

    private static void traverse(TreeNode node, List<Integer> result, int level) {
        if (node == null) return;
        if (level >= result.size()) {
            result.add(node.val);
        }
        traverse(node.right, result, level + 1);
        traverse(node.left, result, level + 1);
    }
}

