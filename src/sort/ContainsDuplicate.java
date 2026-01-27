package sort;

import java.util.HashSet;
import java.util.Set;

public class ContainsDuplicate {
    public static boolean compute(int[] nums){
        Set<Integer> selected = new HashSet<>();
        for(int num : nums){
            if (selected.contains(num)) {
                return true;
            }
            selected.add(num);
        }
        return false;
    }
}
