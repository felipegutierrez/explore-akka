package org.github.felipegutierrez.explore.recap.codility;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FrogJmpTest {
    @Test
    public void frogJumpTest() {
        FrogJmp frogJmp = new FrogJmp();
        assertTrue(frogJmp.solution(10, 85, 30) == 3);
    }
}
