package linkedlist;

/**
 * Reorder List - LeetCode 143
 * You are given the head of a singly linked-list. The list can be represented as:
 * L0 → L1 → … → Ln - 1 → Ln
 * Reorder the list to be on the following form:
 * L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
 * You may not modify the values in the list's nodes. Only nodes themselves may be changed.
 */
public class ReorderList {

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
     * Solution 1: Find Middle, Reverse Second Half, Merge
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static void compute(ListNode head) {
        if (head == null || head.next == null) return;

        ListNode middle = findMiddle(head);
        ListNode secondHalf = reverse(middle.next);
        middle.next = null;

        merge(head, secondHalf);
    }

    private static ListNode findMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private static ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        return prev;
    }

    private static void merge(ListNode first, ListNode second) {
        while (second != null) {
            ListNode firstNext = first.next;
            ListNode secondNext = second.next;

            first.next = second;
            second.next = firstNext;

            first = firstNext;
            second = secondNext;
        }
    }

    /**
     * Solution 2: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static void computeWithStack(ListNode head) {
        if (head == null || head.next == null) return;

        java.util.Stack<ListNode> stack = new java.util.Stack<>();
        ListNode current = head;
        int count = 0;

        while (current != null) {
            stack.push(current);
            current = current.next;
            count++;
        }

        current = head;
        for (int i = 0; i < count / 2; i++) {
            ListNode next = current.next;
            ListNode fromStack = stack.pop();
            current.next = fromStack;
            fromStack.next = next;
            current = next;
        }
        current.next = null;
    }
}

