package linkedlist;

/**
 * Palindrome Linked List - LeetCode 234
 * Given the head of a singly linked list, return true if it is a palindrome or false otherwise.
 */
public class PalindromeLinkedList {

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
     * Solution 1: Reverse Second Half and Compare
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithReversal(ListNode head) {
        if (head == null || head.next == null) return true;

        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode secondHalf = reverse(slow.next);
        ListNode firstHalf = head;

        while (secondHalf != null) {
            if (firstHalf.val != secondHalf.val) {
                return false;
            }
            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }

        return true;
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

    /**
     * Solution 2: Using Stack
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithStack(ListNode head) {
        java.util.Stack<Integer> stack = new java.util.Stack<>();
        ListNode current = head;

        while (current != null) {
            stack.push(current.val);
            current = current.next;
        }

        current = head;
        while (current != null) {
            if (current.val != stack.pop()) {
                return false;
            }
            current = current.next;
        }

        return true;
    }

    /**
     * Solution 3: Recursive
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    private static ListNode front;

    public static boolean computeRecursive(ListNode head) {
        front = head;
        return checkRecursive(head);
    }

    private static boolean checkRecursive(ListNode current) {
        if (current != null) {
            if (!checkRecursive(current.next)) {
                return false;
            }
            if (current.val != front.val) {
                return false;
            }
            front = front.next;
        }
        return true;
    }
}

