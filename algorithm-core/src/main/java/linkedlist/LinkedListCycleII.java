package linkedlist;

import java.util.HashSet;
import java.util.Set;

/**
 * Linked List Cycle II - LeetCode 142
 * Given the head of a linked list, return the node where the cycle begins.
 * If there is no cycle, return null.
 * There is a cycle in a linked list if there is some node in the list that can be reached
 * again by continuously following the next pointer. Internally, pos is used to denote the
 * index of the node that tail's next pointer is connected to (0-indexed).
 */
public class LinkedListCycleII {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    /**
     * Solution 1: Two Pointers (Floyd's Cycle Detection)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static ListNode computeWithTwoPointers(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                ListNode pointer = head;
                while (pointer != slow) {
                    pointer = pointer.next;
                    slow = slow.next;
                }
                return pointer;
            }
        }

        return null;
    }

    /**
     * Solution 2: Using HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static ListNode computeWithHashSet(ListNode head) {
        Set<ListNode> visited = new HashSet<>();
        ListNode current = head;

        while (current != null) {
            if (visited.contains(current)) {
                return current;
            }
            visited.add(current);
            current = current.next;
        }

        return null;
    }
}

