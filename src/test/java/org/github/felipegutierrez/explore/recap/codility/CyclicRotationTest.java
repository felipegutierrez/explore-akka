package org.github.felipegutierrez.explore.recap.codility;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CyclicRotationTest {
    @Test
    public void cyclicRotationTestSimple() {
        CyclicRotation cyclicRotation = new CyclicRotation();

        int[] A = {3, 8, 9, 7, 6};
        int K = 3;
        int[] expectedA = {9, 7, 6, 3, 8};
        int[] resultA = cyclicRotation.solution(A, K);

        assertTrue(expectedA.length == resultA.length);
        for (int i = 0; i < expectedA.length; i++) {
            assertTrue(expectedA[i] == resultA[i]);
        }

        int[] A1 = {0, 0, 0};
        int K1 = 1;
        int[] expectedA1 = {0, 0, 0};
        int[] resultA1 = cyclicRotation.solution(A1, K1);

        assertTrue(expectedA1.length == resultA1.length);
        for (int i = 0; i < expectedA1.length; i++) {
            assertTrue(expectedA1[i] == resultA1[i]);
        }

        int[] A2 = {1, 2, 3, 4};
        int K2 = 4;
        int[] expectedA2 = {1, 2, 3, 4};
        int[] resultA2 = cyclicRotation.solution(A2, K2);

        assertTrue(expectedA2.length == resultA2.length);
        for (int i = 0; i < expectedA2.length; i++) {
            assertTrue(expectedA2[i] == resultA2[i]);
        }
    }

    @Test
    public void cyclicRotationTestDifference() {
        CyclicRotation cyclicRotation = new CyclicRotation();

        int[] A = {3, 8, 9, 7, 6};
        int K = 3;
        int[] expectedA = {9, 7, 6, 3, 8};
        int[] resultA = cyclicRotation.solutionUsingDifferenceFromK(A, K);

        assertTrue(expectedA.length == resultA.length);
        for (int i = 0; i < expectedA.length; i++) {
            assertTrue(expectedA[i] == resultA[i]);
        }

        int[] A1 = {0, 0, 0};
        int K1 = 1;
        int[] expectedA1 = {0, 0, 0};
        int[] resultA1 = cyclicRotation.solutionUsingDifferenceFromK(A1, K1);

        assertTrue(expectedA1.length == resultA1.length);
        for (int i = 0; i < expectedA1.length; i++) {
            assertTrue(expectedA1[i] == resultA1[i]);
        }

        int[] A2 = {1, 2, 3, 4};
        int K2 = 4;
        int[] expectedA2 = {1, 2, 3, 4};
        int[] resultA2 = cyclicRotation.solutionUsingDifferenceFromK(A2, K2);

        assertTrue(expectedA2.length == resultA2.length);
        for (int i = 0; i < expectedA2.length; i++) {
            assertTrue(expectedA2[i] == resultA2[i]);
        }
    }

    @Test
    public void cyclicRotationTestMod() {
        CyclicRotation cyclicRotation = new CyclicRotation();

        int[] A = {3, 8, 9, 7, 6};
        int K = 3;
        int[] expectedA = {9, 7, 6, 3, 8};
        int[] resultA = cyclicRotation.solutionUsingMod(A, K);

        assertTrue(expectedA.length == resultA.length);
        for (int i = 0; i < expectedA.length; i++) {
            assertTrue(expectedA[i] == resultA[i]);
        }

        int[] A1 = {0, 0, 0};
        int K1 = 1;
        int[] expectedA1 = {0, 0, 0};
        int[] resultA1 = cyclicRotation.solutionUsingMod(A1, K1);

        assertTrue(expectedA1.length == resultA1.length);
        for (int i = 0; i < expectedA1.length; i++) {
            assertTrue(expectedA1[i] == resultA1[i]);
        }

        int[] A2 = {1, 2, 3, 4};
        int K2 = 4;
        int[] expectedA2 = {1, 2, 3, 4};
        int[] resultA2 = cyclicRotation.solutionUsingMod(A2, K2);

        assertTrue(expectedA2.length == resultA2.length);
        for (int i = 0; i < expectedA2.length; i++) {
            assertTrue(expectedA2[i] == resultA2[i]);
        }
    }
}
