package org.github.felipegutierrez.explore.recap.codility;

import java.util.Arrays;
import java.util.List;

/**
 * https://app.codility.com/programmers/lessons/1-iterations/binary_gap/
 */
public class BinaryGap {
    // public static void main(String[] args) {
    public void run() {
        BinaryGap binaryGap = new BinaryGap();
        List<Integer> list = Arrays.asList(1041, 32, 9, 529, 20, Integer.MAX_VALUE);
        list.forEach(i -> {
            binaryGap.solution(i);
            binaryGap.solutionBitManipulation(i);
        });
    }

    public int solution(int N) {
        int largestGap = 0;
        String binary = Integer.toBinaryString(N);
        String binaryReverse = new StringBuffer(binary).reverse().toString();
        String binaryTrim = binaryReverse.substring(binaryReverse.indexOf("1"));
        String[] array = binaryTrim.split("1");
        for (String part : array) {
            if (part.length() > largestGap) largestGap = part.length();
        }
        System.out.println("N: " + N + " - binary: " + binary + " - reverse: " + binaryReverse + " - binaryTrim: " + binaryTrim + " - largest gap: " + largestGap);
        return largestGap;
    }

    public int solutionBitManipulation(int N) {
        int originalN = N;
        String originalBinary = Integer.toBinaryString(N);
        int largestGap = 0;
        int currentGap = 0;
        boolean counting = false;
        String binary = "";
        while (N > 0) {
            binary = Integer.toBinaryString(N);
            if (counting == false) { // for the first "1"
                if ((N & 1) == 1) { // the AND bit operation, when we find the first bit that is equal to 1 we start counting
                    counting = true;
                }
            } else {
                if ((N & 1) == 0) currentGap++; // the AND bit operations says that bit is equal to 0, so we count
                else { // the AND bit operations says that bit is equal to 1, so we have to restart the counting
                    if (currentGap > largestGap) largestGap = currentGap; // a new largest gap was fount
                    currentGap = 0; // reset current gap counting
                }
            }
            // print(N, binary, currentGap, largestGap, false);
            N = N >> 1; // move one bit to the right
        }
        // print(N, binary, currentGap, largestGap, true);
        System.out.println("bit wise - N: " + originalN + " - binary: " + originalBinary + " - largest gap: " + largestGap);
        return largestGap;
    }

    public void print(int N, String binary, int currentGap, int largestGap, boolean scape) {
        System.out.print(N + " bin(" + binary + ") curr(" + currentGap + ") larg(" + largestGap + ") - ");
        if (scape) System.out.println();
    }
}
