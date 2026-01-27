package linkedlist;

/**
 * Merge Two Sorted Lists - LeetCode 21
 * You are given the heads of two sorted linked lists list1 and list2.
 * Merge the two lists in a one sorted list. The list should be made by splicing
 * together the nodes of the first two lists.
 * Return the head of the merged linked list.
 */
public class MergeTwoSortedLists {

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
     * Time Complexity: O(n + m)
     * Space Complexity: O(1)
     */
    public static ListNode computeIterative(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                curr.next = list1;
                list1 = list1.next;
            } else {
                curr.next = list2;
                list2 = list2.next;
            }
            curr = curr.next;
        }

        curr.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }

    /**
     * Solution 2: Recursive Approach
     * Time Complexity: O(n + m)
     * Space Complexity: O(n + m) for recursion stack
     */
    public static ListNode computeRecursive(ListNode list1, ListNode list2) {
        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }

        if (list1.val <= list2.val) {
            list1.next = computeRecursive(list1.next, list2);
            return list1;
        } else {
            list2.next = computeRecursive(list1, list2.next);
            return list2;
        }
    }

    /**
     * Solution 3: Using In-Place Merging
     * Time Complexity: O(n + m)
     * Space Complexity: O(1)
     */
    public static ListNode computeInPlace(ListNode list1, ListNode list2) {
        if (list1 == null)
            return list2;
        if (list2 == null)
            return list1;

        if (list1.val > list2.val) {
            ListNode temp = list1;
            list1 = list2;
            list2 = temp;
        }

        ListNode result = list1;
        while (list1 != null && list2 != null) {
            ListNode temp = null;
            while (list1 != null && list1.val <= list2.val) {
                temp = list1;
                list1 = list1.next;
            }
            temp.next = list2;

            ListNode swap = list1;
            list1 = list2;
            list2 = swap;
        }
        return result;
    }
}

