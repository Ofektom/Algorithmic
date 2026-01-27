package linkedlist;

import java.util.HashSet;
import java.util.Set;

/**
 * Linked List Cycle - LeetCode 141
 * Given head, the head of a linked list, determine if the linked list has a
 * cycle in it.
 * There is a cycle in a linked list if there is some node in the list that can
 * be reached again by continuously following the next pointer.
 */
public class LinkedListCycle {

    /**
     * Definition for singly-linked list node
     */
    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    /**
     * Solution 1: Using HashSet
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static boolean computeWithHashSet(ListNode head) {
        Set<ListNode> seen = new HashSet<>();
        ListNode curr = head;

        while (curr != null) {
            if (seen.contains(curr)) {
                return true;
            }
            seen.add(curr);
            curr = curr.next;
        }
        return false;
    }

    /**
     * Solution 2: Floyd's Cycle Detection (Two Pointers - Tortoise and Hare)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithTwoPointers(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode fast = head.next;

        while (fast != null && fast.next != null) {
            if (slow == fast) {
                return true;
            }
            slow = slow.next;
            fast = fast.next.next;
        }
        return false;
    }

    /**
     * Solution 3: Using ArrayList
     * Time Complexity: O(n²) due to contains() check
     * Space Complexity: O(n)
     */
    public static boolean computeWithArrayList(ListNode head) {
        java.util.ArrayList<ListNode> list = new java.util.ArrayList<>();
        ListNode curr = head;

        while (curr != null) {
            if (list.contains(curr)) {
                return true;
            }
            list.add(curr);
            curr = curr.next;
        }
        return false;
    }

    /**
     * Solution 4: Marking Visited Nodes (Modifies list)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static boolean computeWithMarking(ListNode head) {
        ListNode curr = head;
        ListNode marker = new ListNode(Integer.MAX_VALUE);

        while (curr != null) {
            if (curr.next == marker) {
                return true;
            }
            ListNode next = curr.next;
            curr.next = marker;
            curr = next;
        }
        return false;
    }
}

