package linkedlist;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Merge k Sorted Lists - LeetCode 23
 * You are given an array of k linked-lists lists, each linked-list is sorted in ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 */
public class MergeKSortedLists {

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
     * Solution 1: Using Min Heap (Priority Queue)
     * Time Complexity: O(n log k) where n is total nodes, k is number of lists
     * Space Complexity: O(k)
     */
    public static ListNode computeWithHeap(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));

        for (ListNode list : lists) {
            if (list != null) {
                minHeap.offer(list);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (!minHeap.isEmpty()) {
            ListNode node = minHeap.poll();
            current.next = node;
            current = current.next;

            if (node.next != null) {
                minHeap.offer(node.next);
            }
        }

        return dummy.next;
    }

    /**
     * Solution 2: Divide and Conquer (Merge pairs)
     * Time Complexity: O(n log k)
     * Space Complexity: O(1)
     */
    public static ListNode computeWithDivideConquer(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        return mergeLists(lists, 0, lists.length - 1);
    }

    private static ListNode mergeLists(ListNode[] lists, int left, int right) {
        if (left == right) return lists[left];

        int mid = left + (right - left) / 2;
        ListNode leftList = mergeLists(lists, left, mid);
        ListNode rightList = mergeLists(lists, mid + 1, right);

        return mergeTwoLists(leftList, rightList);
    }

    private static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        current.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

    /**
     * Solution 3: Merge one by one
     * Time Complexity: O(k * n)
     * Space Complexity: O(1)
     */
    public static ListNode computeIterative(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        ListNode result = lists[0];
        for (int i = 1; i < lists.length; i++) {
            result = mergeTwoLists(result, lists[i]);
        }

        return result;
    }

    private static ListNode mergeTwoListsHelper(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        current.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}

