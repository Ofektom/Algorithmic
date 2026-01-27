package trees;

/**
 * Binary Tree Maximum Path Sum - LeetCode 124
 * A path in a binary tree is a sequence of nodes where each pair of adjacent nodes
 * in the sequence has an edge connecting them. A node can only appear in the sequence at most once.
 * Note that the path does not need to pass through the root.
 * The path sum of a path is the sum of the node's values in the path.
 * Given the root of a binary tree, return the maximum path sum of any non-empty path.
 */
public class BinaryTreeMaximumPathSum {

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

    private static int maxSum;

    /**
     * Solution 1: Recursive DFS
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     */
    public static int computeRecursive(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        maxPathSumHelper(root);
        return maxSum;
    }

    private static int maxPathSumHelper(TreeNode node) {
        if (node == null) return 0;
        
        int leftSum = Math.max(0, maxPathSumHelper(node.left));
        int rightSum = Math.max(0, maxPathSumHelper(node.right));
        
        int currentPathSum = node.val + leftSum + rightSum;
        maxSum = Math.max(maxSum, currentPathSum);
        
        return node.val + Math.max(leftSum, rightSum);
    }

    /**
     * Solution 2: Recursive with Return Array
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static int computeWithArray(TreeNode root) {
        int[] result = new int[]{Integer.MIN_VALUE};
        maxPathSumHelper(root, result);
        return result[0];
    }

    private static int maxPathSumHelper(TreeNode node, int[] result) {
        if (node == null) return 0;
        
        int leftSum = Math.max(0, maxPathSumHelper(node.left, result));
        int rightSum = Math.max(0, maxPathSumHelper(node.right, result));
        
        result[0] = Math.max(result[0], node.val + leftSum + rightSum);
        
        return node.val + Math.max(leftSum, rightSum);
    }
}

