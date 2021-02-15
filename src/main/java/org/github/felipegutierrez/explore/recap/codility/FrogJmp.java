package org.github.felipegutierrez.explore.recap.codility;

public class FrogJmp {
    // public static void main(String[] args) {
    public void run() {
        FrogJmp frogJmp = new FrogJmp();
        System.out.println(frogJmp.solution(10, 85, 30));
    }

    public int solution(int X, int Y, int D) {
        return (int) Math.ceil(((double) (Y - X)) / D);
    }
}
