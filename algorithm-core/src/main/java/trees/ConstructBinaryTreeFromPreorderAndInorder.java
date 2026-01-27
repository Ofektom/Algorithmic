package trees;

import java.util.HashMap;
import java.util.Map;

/**
 * Construct Binary Tree from Preorder and Inorder Traversal - LeetCode 105
 * Given two integer arrays preorder and inorder where preorder is the preorder traversal
 * of a binary tree and inorder is the inorder traversal of the same tree, construct and
 * return the binary tree.
 */
public class ConstructBinaryTreeFromPreorderAndInorder {

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
     * Solution 1: Recursive with HashMap
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static TreeNode computeRecursive(int[] preorder, int[] inorder) {
        Map<Integer, Integer> inMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inMap.put(inorder[i], i);
        }
        return buildTree(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1, inMap);
    }

    private static TreeNode buildTree(int[] preorder, int preStart, int preEnd,
                                      int[] inorder, int inStart, int inEnd,
                                      Map<Integer, Integer> inMap) {
        if (preStart > preEnd || inStart > inEnd) {
            return null;
        }

        TreeNode root = new TreeNode(preorder[preStart]);
        int inRoot = inMap.get(root.val);
        int numsLeft = inRoot - inStart;

        root.left = buildTree(preorder, preStart + 1, preStart + numsLeft,
                             inorder, inStart, inRoot - 1, inMap);
        root.right = buildTree(preorder, preStart + numsLeft + 1, preEnd,
                              inorder, inRoot + 1, inEnd, inMap);

        return root;
    }

    /**
     * Solution 2: Recursive without HashMap (Linear Search)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     */
    public static TreeNode computeRecursiveLinear(int[] preorder, int[] inorder) {
        return buildTreeLinear(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1);
    }

    private static TreeNode buildTreeLinear(int[] preorder, int preStart, int preEnd,
                                           int[] inorder, int inStart, int inEnd) {
        if (preStart > preEnd || inStart > inEnd) {
            return null;
        }

        TreeNode root = new TreeNode(preorder[preStart]);
        int inRoot = 0;
        for (int i = inStart; i <= inEnd; i++) {
            if (inorder[i] == root.val) {
                inRoot = i;
                break;
            }
        }

        int numsLeft = inRoot - inStart;
        root.left = buildTreeLinear(preorder, preStart + 1, preStart + numsLeft,
                                   inorder, inStart, inRoot - 1);
        root.right = buildTreeLinear(preorder, preStart + numsLeft + 1, preEnd,
                                    inorder, inRoot + 1, inEnd);

        return root;
    }
}

