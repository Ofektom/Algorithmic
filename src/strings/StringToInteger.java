package strings;

/**
 * String to Integer (atoi) - LeetCode 8
 * Implement the myAtoi(string s) function, which converts a string to a 32-bit
 * signed integer (similar to C/C++'s atoi function).
 */
public class StringToInteger {

    /**
     * Solution 1: Iterative Parsing
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeIterative(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int index = 0;
        int n = s.length();

        // Skip leading whitespace
        while (index < n && s.charAt(index) == ' ') {
            index++;
        }

        if (index == n) {
            return 0;
        }

        // Check sign
        int sign = 1;
        if (s.charAt(index) == '+' || s.charAt(index) == '-') {
            sign = s.charAt(index) == '-' ? -1 : 1;
            index++;
        }

        // Convert digits
        int result = 0;
        while (index < n && Character.isDigit(s.charAt(index))) {
            int digit = s.charAt(index) - '0';

            // Check for overflow
            if (result > Integer.MAX_VALUE / 10
                    || (result == Integer.MAX_VALUE / 10 && digit > Integer.MAX_VALUE % 10)) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            result = result * 10 + digit;
            index++;
        }

        return sign * result;
    }

    /**
     * Solution 2: Using StringBuilder
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int computeWithStringBuilder(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        s = s.trim();
        if (s.length() == 0) {
            return 0;
        }

        StringBuilder sb = new StringBuilder();
        int sign = 1;
        int start = 0;

        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
            sign = s.charAt(0) == '-' ? -1 : 1;
            start = 1;
        }

        for (int i = start; i < s.length() && Character.isDigit(s.charAt(i)); i++) {
            sb.append(s.charAt(i));
        }

        if (sb.length() == 0) {
            return 0;
        }

        try {
            long value = Long.parseLong(sb.toString()) * sign;
            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (value < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) value;
        } catch (NumberFormatException e) {
            return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
    }

    /**
     * Solution 3: Using Recursion
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     */
    public static int computeWithRecursion(String s) {
        s = s.trim();
        if (s.length() == 0) {
            return 0;
        }

        int[] index = { 0 };
        int sign = parseSign(s, index);
        return sign * parseDigits(s, index, 0);
    }

    private static int parseSign(String s, int[] index) {
        if (index[0] < s.length() && (s.charAt(index[0]) == '+' || s.charAt(index[0]) == '-')) {
            int sign = s.charAt(index[0]) == '-' ? -1 : 1;
            index[0]++;
            return sign;
        }
        return 1;
    }

    private static int parseDigits(String s, int[] index, long result) {
        if (index[0] >= s.length() || !Character.isDigit(s.charAt(index[0]))) {
            if (result > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            return (int) result;
        }

        int digit = s.charAt(index[0]) - '0';
        index[0]++;

        long newResult = result * 10 + digit;
        if (newResult > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return parseDigits(s, index, newResult);
    }

    /**
     * Solution 4: State Machine Approach
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeStateMachine(String s) {
        State state = State.START;
        int sign = 1;
        long result = 0;

        for (char c : s.toCharArray()) {
            switch (state) {
                case START:
                    if (c == ' ') {
                        continue;
                    } else if (c == '+' || c == '-') {
                        sign = c == '-' ? -1 : 1;
                        state = State.SIGN;
                    } else if (Character.isDigit(c)) {
                        result = c - '0';
                        state = State.NUMBER;
                    } else {
                        return 0;
                    }
                    break;

                case SIGN:
                    if (Character.isDigit(c)) {
                        result = c - '0';
                        state = State.NUMBER;
                    } else {
                        return 0;
                    }
                    break;

                case NUMBER:
                    if (Character.isDigit(c)) {
                        result = result * 10 + (c - '0');
                        if (sign * result > Integer.MAX_VALUE) {
                            return Integer.MAX_VALUE;
                        }
                        if (sign * result < Integer.MIN_VALUE) {
                            return Integer.MIN_VALUE;
                        }
                    } else {
                        return (int) (sign * result);
                    }
                    break;
            }
        }

        return (int) (sign * result);
    }

    private enum State {
        START, SIGN, NUMBER
    }
}

