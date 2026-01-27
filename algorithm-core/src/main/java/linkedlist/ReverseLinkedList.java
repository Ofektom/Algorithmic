package linkedlist;

/**
 * Reverse Linked List - LeetCode 206
 * Given the head of a singly linked list, reverse the list, and return the
 * reversed list.
 */
public class ReverseLinkedList {

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
     * Solution 1: Iterative Approach
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static ListNode computeIterative(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    /**
     * Solution 2: Recursive Approach
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static ListNode computeRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode reversed = computeRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return reversed;
    }

    /**
     * Solution 3: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static ListNode computeWithStack(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        java.util.Stack<ListNode> stack = new java.util.Stack<>();
        ListNode curr = head;
        while (curr != null) {
            stack.push(curr);
            curr = curr.next;
        }

        ListNode newHead = stack.pop();
        curr = newHead;
        while (!stack.isEmpty()) {
            curr.next = stack.pop();
            curr = curr.next;
        }
        curr.next = null;
        return newHead;
    }
}

