package strings;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Time Conversion
 * Convert a 12-hour time string (hh:mm:ssAM/PM) to 24-hour format (HH:mm:ss).
 */
public class TimeConversion {

    /**
     * Solution 1: Manual Parsing
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public static String computeWithParsing(String s) {
        int hour = Integer.parseInt(s.substring(0, 2));
        boolean pm = s.substring(8).equalsIgnoreCase("PM");
        if (pm && hour != 12) hour += 12;
        if (!pm && hour == 12) hour = 0;
        return String.format("%02d%s", hour, s.substring(2, 8));
    }

    /**
     * Solution 2: java.time Formatter
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public static String computeWithLocalTime(String s) {
        DateTimeFormatter input = DateTimeFormatter.ofPattern("hh:mm:ssa", Locale.ENGLISH);
        return LocalTime.parse(s.toUpperCase(), input).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
