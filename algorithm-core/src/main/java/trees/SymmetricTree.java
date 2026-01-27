package trees;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Symmetric Tree - LeetCode 101
 * Given the root of a binary tree, check whether it is a mirror of itself
 * (i.e., symmetric around its center).
 */
public class SymmetricTree {

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
     * Solution 1: Recursive DFS
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     */
    public static boolean computeRecursive(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isSymmetricRecursive(root.left, root.right);
    }

    private static boolean isSymmetricRecursive(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (left.val != right.val) {
            return false;
        }
        return isSymmetricRecursive(left.left, right.right)
                && isSymmetricRecursive(left.right, right.left);
    }

    /**
     * Solution 2: Iterative with Queue (BFS)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithQueue(TreeNode root) {
        if (root == null) {
            return true;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root.left);
        queue.offer(root.right);

        while (!queue.isEmpty()) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();

            if (left == null && right == null) {
                continue;
            }
            if (left == null || right == null || left.val != right.val) {
                return false;
            }

            queue.offer(left.left);
            queue.offer(right.right);
            queue.offer(left.right);
            queue.offer(right.left);
        }
        return true;
    }

    /**
     * Solution 3: Iterative with Stack (DFS)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStack(TreeNode root) {
        if (root == null) {
            return true;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root.left);
        stack.push(root.right);

        while (!stack.isEmpty()) {
            TreeNode right = stack.pop();
            TreeNode left = stack.pop();

            if (left == null && right == null) {
                continue;
            }
            if (left == null || right == null || left.val != right.val) {
                return false;
            }

            stack.push(left.left);
            stack.push(right.right);
            stack.push(left.right);
            stack.push(right.left);
        }
        return true;
    }

    /**
     * Solution 4: In-order Traversal Comparison
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithInOrder(TreeNode root) {
        if (root == null) {
            return true;
        }

        java.util.ArrayList<Integer> leftList = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> rightList = new java.util.ArrayList<>();

        inOrderLeft(root.left, leftList);
        inOrderRight(root.right, rightList);

        return leftList.equals(rightList);
    }

    private static void inOrderLeft(TreeNode node, java.util.ArrayList<Integer> list) {
        if (node == null) {
            list.add(null);
            return;
        }
        list.add(node.val);
        inOrderLeft(node.left, list);
        inOrderLeft(node.right, list);
    }

    private static void inOrderRight(TreeNode node, java.util.ArrayList<Integer> list) {
        if (node == null) {
            list.add(null);
            return;
        }
        list.add(node.val);
        inOrderRight(node.right, list);
        inOrderRight(node.left, list);
    }
}

