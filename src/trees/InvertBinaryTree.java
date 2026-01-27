package trees;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Invert Binary Tree - LeetCode 226
 * Given the root of a binary tree, invert the tree, and return its root.
 */
public class InvertBinaryTree {

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
     * Solution 1: Recursive (DFS)
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     */
    public static TreeNode computeRecursive(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode left = computeRecursive(root.left);
        TreeNode right = computeRecursive(root.right);

        root.left = right;
        root.right = left;

        return root;
    }

    /**
     * Solution 2: Iterative with Stack (DFS)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static TreeNode computeWithStack(TreeNode root) {
        if (root == null) {
            return null;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();

            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;

            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }
        return root;
    }

    /**
     * Solution 3: Iterative with Queue (BFS)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static TreeNode computeWithQueue(TreeNode root) {
        if (root == null) {
            return null;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;

            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        return root;
    }

    /**
     * Solution 4: Post-order Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static TreeNode computePostOrder(TreeNode root) {
        if (root == null) {
            return null;
        }

        computePostOrder(root.left);
        computePostOrder(root.right);

        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        return root;
    }
}

