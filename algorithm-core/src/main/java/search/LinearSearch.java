package search;

public class LinearSearch {
    public static int compute(int[]num, int tar) {
        for(int i = 0; i < num.length; i++){
            if (num[i] == tar) return i;
        }
        return -1;
    }
}
