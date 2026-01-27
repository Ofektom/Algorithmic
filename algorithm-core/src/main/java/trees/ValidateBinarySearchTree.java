package trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Validate Binary Search Tree - LeetCode 98
 * Given the root of a binary tree, determine if it is a valid binary search tree (BST).
 * A valid BST is defined as follows:
 * - The left subtree of a node contains only nodes with keys less than the node's key.
 * - The right subtree of a node contains only nodes with keys greater than the node's key.
 * - Both the left and right subtrees must also be binary search trees.
 */
public class ValidateBinarySearchTree {

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
     * Solution 1: Recursive with Bounds
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     */
    public static boolean computeRecursive(TreeNode root) {
        return validateBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private static boolean validateBST(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return validateBST(node.left, min, node.val) && 
               validateBST(node.right, node.val, max);
    }

    /**
     * Solution 2: Inorder Traversal (BST should be sorted)
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static boolean computeWithInorder(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        inorderTraversal(root, list);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) <= list.get(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private static void inorderTraversal(TreeNode root, List<Integer> list) {
        if (root == null) return;
        inorderTraversal(root.left, list);
        list.add(root.val);
        inorderTraversal(root.right, list);
    }

    /**
     * Solution 3: Iterative Inorder with Stack
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static boolean computeWithStack(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode prev = null;
        TreeNode current = root;

        while (!stack.isEmpty() || current != null) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            if (prev != null && current.val <= prev.val) {
                return false;
            }
            prev = current;
            current = current.right;
        }
        return true;
    }
}

