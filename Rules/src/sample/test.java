package sample;

import java.util.Arrays;

public class test implements Runnable {

    private Controller kontroler;

    static int[][] matrix = {
            {1,1,1},
            {1,1,1},
            {1,1,1},
            {-1,0,-1},
            {2,2,2},
            {2,2,2},
            {2,2,2}
    };
    static int[][] matrix2 = {
            {2,2,2},
            {2,2,2},
            {2,2,2},
            {-1,0,-1},
            {1,1,1},
            {1,1,1},
            {1,1,1}
    };

    static int[][] matrix3 = {
            {1,1,1},
            {1,1,1},
            {1,0,1},
            {-1,1,-1},
            {2,2,2},
            {2,2,2},
            {2,2,2}
    };

    @Override
    public void run() {
        while(true){
            try {
                int[][] board;
                Thread.sleep(2000);
                kontroler.fillBoard(matrix);
                board = kontroler.getBoard();
                System.out.println(Arrays.deepToString(board));
                Thread.sleep(2000);
                kontroler.fillBoard(matrix3);
                board = kontroler.getBoard();
                System.out.println(Arrays.deepToString(board));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public test(Controller kontroler) {
        this.kontroler = kontroler;
    }
}
