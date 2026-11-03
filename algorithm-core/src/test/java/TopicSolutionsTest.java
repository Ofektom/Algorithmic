import arrays.BirthdayCakeCandles;
import arrays.DiagonalDifference;
import arrays.MiniMaxSum;
import arrays.PlusMinus;
import arrays.SalesByMatch;
import greedy.JumpingOnTheClouds;
import org.junit.jupiter.api.Test;
import strings.CaesarCipher;
import strings.CountingValleys;
import strings.RepeatedString;
import strings.TimeConversion;

import static org.junit.jupiter.api.Assertions.*;

class TopicSolutionsTest {

    @Test
    void diagonalDifference() {
        int[][] matrix = { { 11, 2, 4 }, { 4, 5, 6 }, { 10, 8, -12 } };
        assertEquals(15, DiagonalDifference.computeWithSinglePass(matrix));
        assertEquals(15, DiagonalDifference.computeWithFullScan(matrix));
        assertEquals(0, DiagonalDifference.computeWithSinglePass(new int[][] { { 7 } }));
        assertEquals(0, DiagonalDifference.computeWithFullScan(new int[][] { { 7 } }));
    }

    @Test
    void plusMinus() {
        int[] arr = { -4, 3, -9, 0, 4, 1 };
        assertEquals("0.500000 0.333333 0.166667", PlusMinus.computeWithCounting(arr));
        assertEquals("0.500000 0.333333 0.166667", PlusMinus.computeWithStreams(arr));
    }

    @Test
    void miniMaxSum() {
        assertArrayEquals(new long[] { 10, 14 }, MiniMaxSum.computeWithSinglePass(new int[] { 1, 2, 3, 4, 5 }));
        assertArrayEquals(new long[] { 10, 14 }, MiniMaxSum.computeWithSort(new int[] { 5, 3, 1, 4, 2 }));
        // Sums exceed int range
        int[] big = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE };
        long expected = 4L * Integer.MAX_VALUE;
        assertArrayEquals(new long[] { expected, expected }, MiniMaxSum.computeWithSinglePass(big));
        assertArrayEquals(new long[] { expected, expected }, MiniMaxSum.computeWithSort(big));
    }

    @Test
    void birthdayCakeCandles() {
        assertEquals(2, BirthdayCakeCandles.computeWithSinglePass(new int[] { 3, 2, 1, 3 }));
        assertEquals(2, BirthdayCakeCandles.computeWithSort(new int[] { 3, 2, 1, 3 }));
        assertEquals(4, BirthdayCakeCandles.computeWithSinglePass(new int[] { 5, 5, 5, 5 }));
        assertEquals(4, BirthdayCakeCandles.computeWithSort(new int[] { 5, 5, 5, 5 }));
    }

    @Test
    void timeConversion() {
        String[][] cases = {
                { "07:05:45PM", "19:05:45" },
                { "12:00:00AM", "00:00:00" },
                { "12:45:54PM", "12:45:54" },
                { "01:00:00AM", "01:00:00" },
        };
        for (String[] c : cases) {
            assertEquals(c[1], TimeConversion.computeWithParsing(c[0]), c[0]);
            assertEquals(c[1], TimeConversion.computeWithLocalTime(c[0]), c[0]);
        }
    }

    @Test
    void countingValleys() {
        assertEquals(1, CountingValleys.computeWithAltitude(8, "UDDDUDUU"));
        assertEquals(2, CountingValleys.computeWithAltitude(12, "DDUUDDUDUUUD"));
        assertEquals(0, CountingValleys.computeWithAltitude(2, "UD"));
    }

    @Test
    void salesByMatch() {
        int[] socks = { 10, 20, 20, 10, 10, 30, 50, 10, 20 };
        assertEquals(3, SalesByMatch.computeWithHashSet(socks));
        assertEquals(3, SalesByMatch.computeWithSort(socks));
        assertEquals(0, SalesByMatch.computeWithHashSet(new int[] { 1, 2, 3 }));
        assertEquals(0, SalesByMatch.computeWithSort(new int[] { 1, 2, 3 }));
    }

    @Test
    void jumpingOnTheClouds() {
        int[] clouds = { 0, 0, 1, 0, 0, 1, 0 };
        assertEquals(4, JumpingOnTheClouds.computeWithGreedy(clouds));
        assertEquals(4, JumpingOnTheClouds.computeWithDP(clouds));
        int[] other = { 0, 0, 0, 1, 0, 0 };
        assertEquals(3, JumpingOnTheClouds.computeWithGreedy(other));
        assertEquals(3, JumpingOnTheClouds.computeWithDP(other));
    }

    @Test
    void repeatedString() {
        assertEquals(7, RepeatedString.computeWithMath("aba", 10));
        assertEquals(7, RepeatedString.computeWithBruteForce("aba", 10));
        assertEquals(1_000_000_000_000L, RepeatedString.computeWithMath("a", 1_000_000_000_000L));
        assertEquals(0, RepeatedString.computeWithMath("bcd", 50));
    }

    @Test
    void caesarCipher() {
        assertEquals("okffng-Qwvb", CaesarCipher.computeWithShift("middle-Outz", 2));
        assertEquals("okffng-Qwvb", CaesarCipher.computeWithLookup("middle-Outz", 2));
        // Shift larger than the alphabet wraps around
        assertEquals("Bcd", CaesarCipher.computeWithShift("Abc", 27));
        assertEquals("Bcd", CaesarCipher.computeWithLookup("Abc", 27));
    }
}
