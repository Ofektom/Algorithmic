package trees;

/**
 * Lowest Common Ancestor of a Binary Tree - LeetCode 236
 * Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.
 * According to the definition of LCA on Wikipedia: "The lowest common ancestor is defined
 * between two nodes p and q as the lowest node in T that has both p and q as descendants
 * (where we allow a node to be a descendant of itself)."
 */
public class LowestCommonAncestorOfBinaryTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    /**
     * Solution 1: Recursive DFS
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     */
    public static TreeNode computeRecursive(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = computeRecursive(root.left, p, q);
        TreeNode right = computeRecursive(root.right, p, q);

        if (left != null && right != null) {
            return root;
        }

        return left != null ? left : right;
    }

    /**
     * Solution 2: DFS with Path Tracking
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public static TreeNode computeWithPath(TreeNode root, TreeNode p, TreeNode q) {
        java.util.List<TreeNode> pathP = new java.util.ArrayList<>();
        java.util.List<TreeNode> pathQ = new java.util.ArrayList<>();

        findPath(root, p, pathP);
        findPath(root, q, pathQ);

        int i = 0;
        while (i < pathP.size() && i < pathQ.size() && pathP.get(i) == pathQ.get(i)) {
            i++;
        }

        return i > 0 ? pathP.get(i - 1) : null;
    }

    private static boolean findPath(TreeNode root, TreeNode target, java.util.List<TreeNode> path) {
        if (root == null) return false;

        path.add(root);

        if (root == target) return true;

        if (findPath(root.left, target, path) || findPath(root.right, target, path)) {
            return true;
        }

        path.remove(path.size() - 1);
        return false;
    }
}

