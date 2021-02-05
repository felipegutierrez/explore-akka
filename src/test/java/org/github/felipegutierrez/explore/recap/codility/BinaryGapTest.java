package org.github.felipegutierrez.explore.recap.codility;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BinaryGapTest {
    @Test
    public void binaryGeneralGapTest() {
        BinaryGap binaryGap = new BinaryGap();
        assertTrue(binaryGap.solution(1041) == 5);
        assertTrue(binaryGap.solution(32) == 0);
        assertTrue(binaryGap.solution(15) == 0);
        assertTrue(binaryGap.solution(9) == 2);
        assertTrue(binaryGap.solution(529) == 4);
        assertTrue(binaryGap.solution(20) == 1);
        assertTrue(binaryGap.solution(Integer.MAX_VALUE) == 0);
    }

    @Test
    public void binaryBitWiseGapTest() {
        BinaryGap binaryGap = new BinaryGap();
        assertTrue(binaryGap.solutionBitManipulation(1041) == 5);
        assertTrue(binaryGap.solutionBitManipulation(32) == 0);
        assertTrue(binaryGap.solutionBitManipulation(15) == 0);
        assertTrue(binaryGap.solutionBitManipulation(9) == 2);
        assertTrue(binaryGap.solutionBitManipulation(529) == 4);
        assertTrue(binaryGap.solutionBitManipulation(20) == 1);
        assertTrue(binaryGap.solutionBitManipulation(Integer.MAX_VALUE) == 0);
    }
}
