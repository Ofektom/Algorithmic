package trees;

/**
 * Subtree of Another Tree - LeetCode 572
 * Given the roots of two binary trees root and subRoot, return true if there is a subtree
 * of root with the same structure and node values of subRoot and false otherwise.
 * A subtree of a binary tree tree is a tree that consists of a node in tree and all of
 * this node's descendants. The tree tree could also be considered as a subtree of itself.
 */
public class SubtreeOfAnotherTree {

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
     * Time Complexity: O(m * n) where m is nodes in root, n in subRoot
     * Space Complexity: O(h) where h is height of root
     */
    public static boolean computeRecursive(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;
        if (isSameTree(root, subRoot)) return true;
        return computeRecursive(root.left, subRoot) || computeRecursive(root.right, subRoot);
    }

    private static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        if (p.val != q.val) return false;
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    /**
     * Solution 2: DFS with Serialization
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     */
    public static boolean computeWithSerialization(TreeNode root, TreeNode subRoot) {
        StringBuilder rootStr = new StringBuilder();
        StringBuilder subRootStr = new StringBuilder();
        serialize(root, rootStr);
        serialize(subRoot, subRootStr);
        return rootStr.toString().contains(subRootStr.toString());
    }

    private static void serialize(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("#");
            return;
        }
        sb.append(",").append(node.val).append(",");
        serialize(node.left, sb);
        serialize(node.right, sb);
    }
}

