package trees;

/**
 * Maximum Depth of Binary Tree - LeetCode 104
 * Given the root of a binary tree, return its maximum depth.
 * A binary tree's maximum depth is the number of nodes along the longest path
 * from the root node down to the farthest leaf node.
 */
public class MaximumDepthOfBinaryTree {

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
    public static int computeRecursive(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(computeRecursive(root.left), computeRecursive(root.right));
    }

    /**
     * Solution 2: Iterative DFS with Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithStack(TreeNode root) {
        if (root == null) {
            return 0;
        }

        java.util.Stack<TreeNode> nodeStack = new java.util.Stack<>();
        java.util.Stack<Integer> depthStack = new java.util.Stack<>();
        nodeStack.push(root);
        depthStack.push(1);
        int maxDepth = 0;

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int depth = depthStack.pop();
            maxDepth = Math.max(maxDepth, depth);

            if (node.right != null) {
                nodeStack.push(node.right);
                depthStack.push(depth + 1);
            }
            if (node.left != null) {
                nodeStack.push(node.left);
                depthStack.push(depth + 1);
            }
        }
        return maxDepth;
    }

    /**
     * Solution 3: Iterative BFS (Level Order)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithBFS(TreeNode root) {
        if (root == null) {
            return 0;
        }

        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            depth++;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
        return depth;
    }

    /**
     * Solution 4: Post-order Traversal with Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computePostOrder(TreeNode root) {
        if (root == null) {
            return 0;
        }

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        java.util.HashMap<TreeNode, Integer> depthMap = new java.util.HashMap<>();
        stack.push(root);
        depthMap.put(root, 1);
        int maxDepth = 0;

        while (!stack.isEmpty()) {
            TreeNode node = stack.peek();

            if (node.left != null && !depthMap.containsKey(node.left)) {
                stack.push(node.left);
                depthMap.put(node.left, depthMap.get(node) + 1);
            } else if (node.right != null && !depthMap.containsKey(node.right)) {
                stack.push(node.right);
                depthMap.put(node.right, depthMap.get(node) + 1);
            } else {
                stack.pop();
                maxDepth = Math.max(maxDepth, depthMap.get(node));
            }
        }
        return maxDepth;
    }
}

