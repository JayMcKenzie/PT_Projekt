package sample;

import game.Game;
import game.Player;
import game.PlayerType;
import kotlin.Pair;

import java.util.ArrayList;

public class Rules implements Runnable {

    public Rules(Controller kontroler) {
        this.kontroler = kontroler;
    }
    Controller kontroler;

    int[][] matrix_camera = test.matrix3;
    int[][] matrix_board = test.matrix;

    //int currentPlayer = 1;
    //Boolean move = false;

    public ArrayList<Integer> checkDifferences(){

        ArrayList<Integer> change = new ArrayList<>();

        for(int i = 0; i < matrix_camera.length; i++){
            for(int j = 0; j < matrix_camera[0].length; j++){
                if (matrix_camera[i][j] != matrix_board[i][j]) { change.add(i); change.add(j); }
            }
        }

        return change;
    }

    public Boolean checkMove(ArrayList<Integer> change){

        if(change.size() == 4)
        {
            int x1 = change.get(0);
            int y1 = change.get(1);
            int x2 = change.get(2);
            int y2 = change.get(3);

            if(checkLength(x1,y1,x2,y2) == 1){
                return true;
            }
        }
        return false;
    }

    public Boolean checkBicie(ArrayList<Integer> change){
        if(change.size() == 6)
        {
            int x1 = change.get(0);
            int y1 = change.get(1);
            int x2 = change.get(2);
            int y2 = change.get(3);
            int x3 = change.get(4);
            int y3 = change.get(5);
            if(matrix_camera[x2][y2] != 0)         //w przypadku bicia 'środkowe' pole zawsze będzie puste
                return false;
            else{
                if(matrix_camera[x1][y1] == 0 && matrix_camera[x3][y3] == 0) return false;      //wszystkie pola nie mogą być puste
                else {
                    return matrix_camera[x1][y1] != matrix_board[x1][y1] && matrix_camera[x3][y3] != matrix_board[x3][y3];
                }
            }
        }
        else
            return false;
    }

    public int checkLength(int x1, int y1, int x2, int y2){
        if(x1 == 3 || x2 == 3){ return Math.abs(y2-y1); }
        else{ return (Math.abs(x2-x1) + Math.abs(y2-y1)); }
    }

    public int getFieldNumber(int x, int y){
        return (3*y + x + 1);
    }

    @Override
    public void run(){
        Game game = new Game(new Player(PlayerType.Human,1), new Player(PlayerType.Human,2), kontroler);
        game.start();
        kontroler.fillBoard(matrix_camera);
        while(true){
            try{
                Thread.sleep(1000);
                matrix_camera = test.matrix3;
                ArrayList<Integer> change = checkDifferences();
                Boolean ifMove = checkMove(change);
                Boolean ifBicie = checkBicie(change);
                if (ifMove && !game.getCaptureRequired())
                {
                    int pole1 = getFieldNumber(change.get(0),change.get(1));
                    int pole2 = getFieldNumber(change.get(2),change.get(3));
                    if(matrix_camera[change.get(0)][change.get(1)] !=0){
                        int kolor = matrix_camera[change.get(0)][change.get(1)];
                        if(matrix_board[change.get(0)][change.get(1)] != kolor){
                            game.setNewMove(new Pair<Integer, Integer>(pole2,pole1));
                        }
                    }
                    else
                        game.setNewMove(new Pair<Integer, Integer>(pole1,pole2));
                }
                if (ifBicie && game.getCaptureRequired()){
                    int pole1 = getFieldNumber(change.get(0),change.get(1));
                    int pole2 = getFieldNumber(change.get(2),change.get(3));
                    int pole3 = getFieldNumber(change.get(4),change.get(5));
                    if(matrix_camera[change.get(0)][change.get(1)] != 0){
                      int kolor = matrix_camera[change.get(0)][change.get(1)];
                      if (matrix_board[change.get(0)][change.get(1)] != kolor){
                          System.out.println("tutaj pojawił się pionek po biciu: " + pole1);
                          game.setNewMove(new Pair<Integer, Integer>(pole3, pole1));
                      }

                    }
                    if(matrix_camera[change.get(4)][change.get(5)] != 0){
                        int kolor = matrix_camera[change.get(4)][change.get(5)];
                        if (matrix_board[change.get(4)][change.get(5)] != kolor){
                            System.out.println("tutaj pojawił się pionek po biciu: " + pole3);
                            game.setNewMove(new Pair<Integer, Integer>(pole1, pole3));
                        }

                    }
                    if(matrix_camera[change.get(2)][change.get(3)] == 0 && matrix_board[change.get(2)][change.get(3)] != 0){
                        int kolor = matrix_board[change.get(2)][change.get(3)];
                        System.out.println("Zbity został pionek gracza " + kolor +" z pola " + pole2);
                    }

                }


            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
