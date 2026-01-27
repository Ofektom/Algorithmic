package linkedlist;

/**
 * Remove Nth Node From End of List - LeetCode 19
 * Given the head of a linked list, remove the nth node from the end of the
 * list and return its head.
 */
public class RemoveNthNodeFromEnd {

    /**
     * Definition for singly-linked list node
     */
    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    /**
     * Solution 1: Two Pass (Find length then remove)
     * Time Complexity: O(L) where L is length of list
     * Space Complexity: O(1)
     */
    public static ListNode computeTwoPass(ListNode head, int n) {
        int length = 0;
        ListNode curr = head;
        while (curr != null) {
            length++;
            curr = curr.next;
        }

        if (n == length) {
            return head.next;
        }

        curr = head;
        for (int i = 0; i < length - n - 1; i++) {
            curr = curr.next;
        }
        curr.next = curr.next.next;
        return head;
    }

    /**
     * Solution 2: Two Pointers (One Pass)
     * Time Complexity: O(L)
     * Space Complexity: O(1)
     */
    public static ListNode computeTwoPointers(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode first = dummy;
        ListNode second = dummy;

        for (int i = 0; i <= n; i++) {
            first = first.next;
        }

        while (first != null) {
            first = first.next;
            second = second.next;
        }

        second.next = second.next.next;
        return dummy.next;
    }

    /**
     * Solution 3: Using Stack
     * Time Complexity: O(L)
     * Space Complexity: O(L)
     */
    public static ListNode computeWithStack(ListNode head, int n) {
        java.util.Stack<ListNode> stack = new java.util.Stack<>();
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode curr = dummy;

        while (curr != null) {
            stack.push(curr);
            curr = curr.next;
        }

        for (int i = 0; i < n; i++) {
            stack.pop();
        }

        ListNode nodeToRemove = stack.pop();
        nodeToRemove.next = nodeToRemove.next.next;
        return dummy.next;
    }

    /**
     * Solution 4: Using Recursion
     * Time Complexity: O(L)
     * Space Complexity: O(L) for recursion stack
     */
    public static ListNode computeRecursive(ListNode head, int n) {
        int[] index = { 0 };
        return removeRecursive(head, n, index);
    }

    private static ListNode removeRecursive(ListNode head, int n, int[] index) {
        if (head == null) {
            return null;
        }
        head.next = removeRecursive(head.next, n, index);
        index[0]++;
        if (index[0] == n) {
            return head.next;
        }
        return head;
    }
}

