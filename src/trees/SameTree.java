package trees;

/**
 * Same Tree - LeetCode 100
 * Given the roots of two binary trees p and q, write a function to check if
 * they are the same or not.
 * Two binary trees are considered the same if they are structurally identical,
 * and the nodes have the same value.
 */
public class SameTree {

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
    public static boolean computeRecursive(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if (p == null || q == null) {
            return false;
        }
        if (p.val != q.val) {
            return false;
        }
        return computeRecursive(p.left, q.left) && computeRecursive(p.right, q.right);
    }

    /**
     * Solution 2: Iterative DFS with Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStack(TreeNode p, TreeNode q) {
        java.util.Stack<TreeNode[]> stack = new java.util.Stack<>();
        stack.push(new TreeNode[] { p, q });

        while (!stack.isEmpty()) {
            TreeNode[] nodes = stack.pop();
            TreeNode node1 = nodes[0];
            TreeNode node2 = nodes[1];

            if (node1 == null && node2 == null) {
                continue;
            }
            if (node1 == null || node2 == null || node1.val != node2.val) {
                return false;
            }

            stack.push(new TreeNode[] { node1.left, node2.left });
            stack.push(new TreeNode[] { node1.right, node2.right });
        }
        return true;
    }

    /**
     * Solution 3: Iterative BFS (Level Order)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithBFS(TreeNode p, TreeNode q) {
        java.util.Queue<TreeNode> queue1 = new java.util.LinkedList<>();
        java.util.Queue<TreeNode> queue2 = new java.util.LinkedList<>();
        queue1.offer(p);
        queue2.offer(q);

        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            TreeNode node1 = queue1.poll();
            TreeNode node2 = queue2.poll();

            if (node1 == null && node2 == null) {
                continue;
            }
            if (node1 == null || node2 == null || node1.val != node2.val) {
                return false;
            }

            queue1.offer(node1.left);
            queue1.offer(node1.right);
            queue2.offer(node2.left);
            queue2.offer(node2.right);
        }

        return queue1.isEmpty() && queue2.isEmpty();
    }
}

