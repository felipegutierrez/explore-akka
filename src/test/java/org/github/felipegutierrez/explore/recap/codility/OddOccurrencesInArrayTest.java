package org.github.felipegutierrez.explore.recap.codility;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OddOccurrencesInArrayTest {
    @Test
    public void cyclicRotationTestSimple() {
        OddOccurrencesInArray oddOccurrencesInArray = new OddOccurrencesInArray();
        int[] A = {9, 3, 9, 3, 9, 7};
        int result = oddOccurrencesInArray.solution(A);
        assertTrue(result == 0);

        int[] A1 = {9, 3, 9, 3, 9, 7, 9};
        int result1 = oddOccurrencesInArray.solution(A1);
        assertTrue(result1 == 7);

        int[] A2 = {1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 77, 5, 7};
        int result2 = oddOccurrencesInArray.solution(A2);
        assertTrue(result2 == 77);


        int[] A3 = {1, 3, 3, 77, 7, 1, 5, 7, 1, 1, 3, 5, 7, 7, 3, 5, 5};
        int result3 = oddOccurrencesInArray.solution(A3);
        assertTrue(result3 == 77);
    }
}
