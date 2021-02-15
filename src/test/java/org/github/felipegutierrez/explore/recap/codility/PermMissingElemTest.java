package org.github.felipegutierrez.explore.recap.codility;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PermMissingElemTest {

    @Test
    public void permMissingElemTest1Solution() {
        PermMissingElem permMissingElem = new PermMissingElem();
        assertTrue(permMissingElem.solution(new int[]{2, 3, 1, 5}) == 4);
    }

    @Test
    public void permMissingElemTest2Solution() {
        PermMissingElem permMissingElem = new PermMissingElem();
        assertTrue(permMissingElem.solution(new int[]{2, 3, 1, 5, 7, 6, 9, 8}) == 4);
    }

    @Test
    public void permMissingElemTest3Solution() {
        PermMissingElem permMissingElem = new PermMissingElem();
        assertTrue(permMissingElem.solution(new int[]{2, 3, 1, 5, 7, 6, 4, 9, 8, 11}) == 10);
    }

    @Test
    public void permMissingElemTest1Solution1() {
        PermMissingElem permMissingElem = new PermMissingElem();
        assertTrue(permMissingElem.solution1(new int[]{2, 3, 1, 5}) == 4);
    }

    @Test
    public void permMissingElemTest2Solution1() {
        PermMissingElem permMissingElem = new PermMissingElem();
        assertTrue(permMissingElem.solution1(new int[]{2, 3, 1, 5, 7, 6, 9, 8}) == 4);
    }

    @Test
    public void permMissingElemTest3Solution1() {
        PermMissingElem permMissingElem = new PermMissingElem();
        assertTrue(permMissingElem.solution1(new int[]{2, 3, 1, 5, 7, 6, 4, 9, 8, 11}) == 10);
    }
}
