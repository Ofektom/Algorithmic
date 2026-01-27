package trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Binary Tree Inorder Traversal - LeetCode 94
 * Given the root of a binary tree, return the inorder traversal of its nodes' values.
 */
public class BinaryTreeInorderTraversal {

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
     * Solution 1: Recursive
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     */
    public static List<Integer> computeRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderRecursive(root, result);
        return result;
    }

    private static void inorderRecursive(TreeNode root, List<Integer> result) {
        if (root == null) return;
        inorderRecursive(root.left, result);
        result.add(root.val);
        inorderRecursive(root.right, result);
    }

    /**
     * Solution 2: Iterative with Stack
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static List<Integer> computeWithStack(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (!stack.isEmpty() || current != null) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.val);
            current = current.right;
        }
        return result;
    }

    /**
     * Solution 3: Morris Traversal (Space O(1))
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static List<Integer> computeMorris(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                result.add(current.val);
                current = current.right;
            } else {
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }
                if (predecessor.right == null) {
                    predecessor.right = current;
                    current = current.left;
                } else {
                    predecessor.right = null;
                    result.add(current.val);
                    current = current.right;
                }
            }
        }
        return result;
    }
}

