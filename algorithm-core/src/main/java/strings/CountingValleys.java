package strings;

/**
 * Counting Valleys
 * Given a hike path of 'U' (up) and 'D' (down) steps starting at sea level,
 * return the number of valleys walked through. A valley is a sequence of steps
 * below sea level, starting with a step down from sea level and ending with a
 * step up to sea level.
 */
public class CountingValleys {

    /**
     * Solution 1: Track Altitude
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int computeWithAltitude(int steps, String path) {
        int altitude = 0;
        int valleys = 0;
        for (int i = 0; i < steps; i++) {
            if (path.charAt(i) == 'U') {
                altitude++;
                if (altitude == 0) valleys++;
            } else {
                altitude--;
            }
        }
        return valleys;
    }
}
