package sample;

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


    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(2000);
                kontroler.fillBoard(matrix);
                Thread.sleep(2000);
                kontroler.fillBoard(matrix2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public test(Controller kontroler) {
        this.kontroler = kontroler;
    }
}
