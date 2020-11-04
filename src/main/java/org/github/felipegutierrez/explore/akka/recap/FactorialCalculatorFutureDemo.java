package org.github.felipegutierrez.explore.akka.recap;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class FactorialCalculatorFutureDemo {

    public FactorialCalculatorFutureDemo() {
        // test with 1, 2, 10, 20 threads
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        List<Map<Integer, Future<BigInteger>>> resultList = new ArrayList<>();
        Random random = new Random();
        Date start = new Date();

        for (int i = 0; i < 100; i++) {
            int number = random.nextInt(100) + 10;
            FactorialCalculator factorialCalculator = new FactorialCalculator(number);

            Map<Integer, Future<BigInteger>> result = new HashMap<>();
            result.put(number, executor.submit(factorialCalculator));
            resultList.add(result);
        }
        System.out.println("factorial functions submitted...");

        try {
            for (Map<Integer, Future<BigInteger>> pair : resultList) {
                Optional<Integer> optional = pair.keySet().stream().findFirst();
                if (!optional.isPresent()) {
                    return;
                }
                Integer key = optional.get();
                System.out.printf("Value is: %d%n", key);
                Future<BigInteger> future = pair.get(key);
                BigInteger result = null;
                result = future.get();
                boolean isDone = future.isDone();

                System.out.printf("Result is %d%n", result);
                System.out.printf("Task done: %b%n", isDone);
                System.out.println("--------------------");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("start time : " + getCurrentTimeStamp(start));
        System.out.println("finish time: " + getCurrentTimeStamp(new Date()));
        executor.shutdown();
    }

    public static void main(String[] args) {
        new FactorialCalculatorFutureDemo();
    }

    private static String getCurrentTimeStamp(Date now) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now);
    }

    private static class FactorialCalculator implements Callable<BigInteger> {
        private final int value;

        public FactorialCalculator(int value) {
            this.value = value;
        }

        @Override
        public BigInteger call() throws Exception {
            BigInteger result = BigInteger.valueOf(1);
            if (value == 0 || value == 1) {
                result = BigInteger.valueOf(1);
            } else {
                for (int i = 2; i <= value; i++) {
                    result = result.multiply(BigInteger.valueOf(i));
                }
            }
            TimeUnit.MILLISECONDS.sleep(500);
            return result;
        }
    }
}
