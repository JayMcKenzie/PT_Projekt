package sample;

import java.util.ArrayList;

public class Rules {

    static int[][] matrix = Controller.matrixes.get(0);
    static int[][] matrix2 = Controller.matrixes.get(1);
    int currentPlayer = 1;;
    Boolean move = false;



    public ArrayList<Integer> checkDifferences(){

        ArrayList<Integer> change = new ArrayList<>();

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if (matrix[i][j] != matrix2[i][j]) { change.add(i); change.add(j); }
            }
        }

        return change;
    }

    public void check(){

        matrix2 = matrix;
        matrix = Controller.matrixes.get(0);

        if(checkDifferences().size() == 4){



        }
    }

    /*public Boolean checkMove(ArrayList<Integer> change){

        int x1 = change.get(0);
        int y1 = change.get(1);
        int x2 = change.get(2);
        int y2 = change.get(3);

      //  if(checkLength(x1,y1,x2,y2) == )


    }*/

    public int checkLength(int x1, int y1, int x2, int y2){
        if(x1 == 3 || x2 == 3){ return Math.abs(y2-y1); }
        else{ return (Math.abs(x2-x1) + Math.abs(y2-y1)); }
    }




}
