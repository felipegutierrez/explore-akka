package org.github.felipegutierrez.explore.recap.codility;

/**
 * https://app.codility.com/programmers/lessons/2-arrays/odd_occurrences_in_array/
 */
public class OddOccurrencesInArray {
    public static void main(String[] args) {
        // public void run() {
        OddOccurrencesInArray oddOccurrencesInArray = new OddOccurrencesInArray();
        int[] A = {9, 3, 9, 3, 9, 7, 9};
        int result = 0;
        result = oddOccurrencesInArray.solution(A);
        System.out.println(result);
    }

    public int solution(int[] A) {
        if (A.length % 2 == 0) return 0; // N is an odd integer within the range [1..1,000,000];
        if (A.length > 1_000_000) return 0; // N is an odd integer within the range [1..1,000,000];
        if (A.length == 0) return 0;

        /**
         * Using XOR bitwise
         * The XOR operator compares each binary digit of two integers and
         * gives back 1 if both the compared bits are different.
         * This means that if bits of both the integers are 1 or 0 the result will be 0;
         * otherwise, the result will be 1:
         *     int value1 = 6;
         *     int value2 = 5;
         *     int result = value1 ^ value2;
         *     assertEquals(3, result);
         * 6 - 0110
         * 5 - 0101
         * -----
         * 3 - 0011
         *
         * also
         *
         * 6 - 0110
         * 6 - 0110
         * -----
         * 0 - 0000
         */
        int unpaired = A[0]; // the first element to test
        for (int i = 1; i < A.length; i++) {
            unpaired = unpaired ^ A[i]; // xor
        }
        return unpaired;
    }
}
