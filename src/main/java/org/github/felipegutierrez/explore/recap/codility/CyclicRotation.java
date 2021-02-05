package org.github.felipegutierrez.explore.recap.codility;

import java.util.Arrays;

/**
 * https://app.codility.com/programmers/lessons/2-arrays/cyclic_rotation/
 */
public class CyclicRotation {
    // public static void main(String[] args) {
    public void run() {
        CyclicRotation cyclicRotation = new CyclicRotation();

        int[] A = {3, 8, 9, 7, 6};
        int K = 3;
        int[] result = cyclicRotation.solution(A, K);
        Arrays.stream(result).forEach(v -> {
            System.out.print(v + ", ");
        });
        System.out.println();

        int[] resultDif = cyclicRotation.solutionUsingDifferenceFromK(A, K);
        Arrays.stream(resultDif).forEach(v -> {
            System.out.print(v + ", ");
        });
        System.out.println();

        int[] resultMod = cyclicRotation.solutionUsingMod(A, K);
        Arrays.stream(resultMod).forEach(v -> {
            System.out.print(v + ", ");
        });
        System.out.println();
    }

    public int[] solution(int[] A, int K) {
        int originalK = K;
        int[] result = new int[A.length];
        if (A.length == K) return A; // the size of the array is equal to the qtd of moves
        else {
            while (K > A.length) K = (K - A.length); // the rotation is overlapping
            // System.out.println("K=" + originalK + " new K=" + K);
            int count = 0;
            // the rotation from K - 1 to the end
            for (int i = K - 1; i < A.length; i++) {
                result[count] = A[i];
                count++;
            }
            // the remaining items
            for (int i = 0; i < K - 1; i++) {
                result[count] = A[i];
                count++;
            }
        }
        return result;
    }

    public int[] solutionUsingDifferenceFromK(int[] A, int K) {
        int[] new_array = new int[A.length];

        for (int i = 0; i < A.length; i++) {
            int new_position = 0;
            if ((i + K) < A.length) new_position = i + K;
            else if ((i + K) > A.length) new_position = (i + K) - A.length;
            new_array[new_position] = A[i];
        }
        return new_array;
    }

    public int[] solutionUsingMod(int[] A, int K) {
        int[] new_array = new int[A.length]; // a new array

        for (int i = 0; i < A.length; i++) {
            int new_position = (i + K) % A.length; // using "mod" to do Cyclic Rotation
            // System.out.println("(i + K) = " + (i + K) + " % A.length = " + new_position);
            new_array[new_position] = A[i]; // put A[i] to the new position
        }

        return new_array; // return new array
    }
}
