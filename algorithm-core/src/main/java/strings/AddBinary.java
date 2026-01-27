package strings;

/**
 * Add Binary - LeetCode 67
 * Given two binary strings a and b, return their sum as a binary string.
 */
public class AddBinary {

    /**
     * Solution 1: Iterative with StringBuilder
     * Time Complexity: O(max(|a|, |b|))
     * Space Complexity: O(max(|a|, |b|))
     */
    public static String computeWithStringBuilder(String a, String b) {
        StringBuilder result = new StringBuilder();
        int i = a.length() - 1;
        int j = b.length() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';

            result.append(sum % 2);
            carry = sum / 2;
        }

        return result.reverse().toString();
    }

    /**
     * Solution 2: Using BigInteger (Built-in)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static String computeWithBigInteger(String a, String b) {
        java.math.BigInteger num1 = new java.math.BigInteger(a, 2);
        java.math.BigInteger num2 = new java.math.BigInteger(b, 2);
        return num1.add(num2).toString(2);
    }

    /**
     * Solution 3: Manual Character Array
     * Time Complexity: O(max(|a|, |b|))
     * Space Complexity: O(max(|a|, |b|))
     */
    public static String computeWithArray(String a, String b) {
        int maxLen = Math.max(a.length(), b.length());
        char[] result = new char[maxLen + 1];
        int carry = 0;
        int index = maxLen;

        int i = a.length() - 1;
        int j = b.length() - 1;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';

            result[index--] = (char) ('0' + (sum % 2));
            carry = sum / 2;
        }

        int start = (index == 0) ? 0 : 1;
        return new String(result, start, maxLen + 1 - start);
    }

    /**
     * Solution 4: Recursive
     * Time Complexity: O(max(|a|, |b|))
     * Space Complexity: O(max(|a|, |b|))
     */
    public static String computeRecursive(String a, String b) {
        return addBinaryRecursive(a, b, 0, new StringBuilder()).reverse().toString();
    }

    private static StringBuilder addBinaryRecursive(String a, String b, int carry, StringBuilder sb) {
        if (a.isEmpty() && b.isEmpty() && carry == 0) {
            return sb;
        }

        int sum = carry;
        if (!a.isEmpty()) {
            sum += a.charAt(a.length() - 1) - '0';
            a = a.substring(0, a.length() - 1);
        }
        if (!b.isEmpty()) {
            sum += b.charAt(b.length() - 1) - '0';
            b = b.substring(0, b.length() - 1);
        }

        sb.append(sum % 2);
        return addBinaryRecursive(a, b, sum / 2, sb);
    }
}

