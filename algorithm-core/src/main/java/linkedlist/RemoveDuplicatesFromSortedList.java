package linkedlist;

/**
 * Remove Duplicates from Sorted List - LeetCode 83
 * Given the head of a sorted linked list, delete all duplicates such that each element
 * appears only once. Return the linked list sorted as well.
 */
public class RemoveDuplicatesFromSortedList {

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
     * Solution 1: Iterative (Two Pointers)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static ListNode computeIterative(ListNode head) {
        ListNode current = head;

        while (current != null && current.next != null) {
            if (current.val == current.next.val) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }

        return head;
    }

    /**
     * Solution 2: Recursive
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static ListNode computeRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        head.next = computeRecursive(head.next);

        if (head.val == head.next.val) {
            return head.next;
        }

        return head;
    }

    /**
     * Solution 3: Using HashSet (For unsorted - extra solution)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static ListNode computeWithHashSet(ListNode head) {
        if (head == null) return null;

        java.util.Set<Integer> seen = new java.util.HashSet<>();
        ListNode current = head;
        ListNode prev = null;

        while (current != null) {
            if (seen.contains(current.val)) {
                prev.next = current.next;
            } else {
                seen.add(current.val);
                prev = current;
            }
            current = current.next;
        }

        return head;
    }
}

