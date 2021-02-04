package org.github.felipegutierrez.explore.recap;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FactorialCalculatorFutureDemoTest {

    @Test
    public void computeFactorial() throws Exception {
        if (Runtime.getRuntime().availableProcessors() >= 4) {
            FactorialCalculatorFutureDemo factorialDemo = new FactorialCalculatorFutureDemo(20, 500);
            assertTrue(factorialDemo.compute());
        }
    }
}
