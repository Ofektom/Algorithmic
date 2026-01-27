package linkedlist;

/**
 * Add Two Numbers - LeetCode 2
 * You are given two non-empty linked lists representing two non-negative
 * integers. The digits are stored in reverse order, and each of their nodes
 * contains a single digit. Add the two numbers and return the sum as a linked
 * list.
 */
public class AddTwoNumbers {

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
     * Solution 1: Iterative with Carry
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(max(m, n))
     */
    public static ListNode computeIterative(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;

            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
        }

        return dummy.next;
    }

    /**
     * Solution 2: Recursive
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(max(m, n)) for recursion stack
     */
    public static ListNode computeRecursive(ListNode l1, ListNode l2) {
        return addRecursive(l1, l2, 0);
    }

    private static ListNode addRecursive(ListNode l1, ListNode l2, int carry) {
        if (l1 == null && l2 == null && carry == 0) {
            return null;
        }

        int sum = carry;
        if (l1 != null) {
            sum += l1.val;
            l1 = l1.next;
        }
        if (l2 != null) {
            sum += l2.val;
            l2 = l2.next;
        }

        ListNode result = new ListNode(sum % 10);
        result.next = addRecursive(l1, l2, sum / 10);
        return result;
    }

    /**
     * Solution 3: Using Two Passes (First convert to numbers - may overflow)
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(max(m, n))
     */
    public static ListNode computeWithConversion(ListNode l1, ListNode l2) {
        long num1 = listToNumber(l1);
        long num2 = listToNumber(l2);
        long sum = num1 + num2;
        return numberToList(sum);
    }

    private static long listToNumber(ListNode head) {
        long num = 0;
        long multiplier = 1;
        while (head != null) {
            num += head.val * multiplier;
            multiplier *= 10;
            head = head.next;
        }
        return num;
    }

    private static ListNode numberToList(long num) {
        if (num == 0) {
            return new ListNode(0);
        }

        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (num > 0) {
            curr.next = new ListNode((int) (num % 10));
            curr = curr.next;
            num /= 10;
        }

        return dummy.next;
    }
}

