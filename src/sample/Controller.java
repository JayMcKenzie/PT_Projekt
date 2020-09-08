package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Controller {

    @FXML
    ImageView plansza;

    @FXML
    ArrayList<Circle> fields;         //tablica fieldów (Circle)

    @FXML
    TextArea textArea;

    public static ArrayList<int[][]> matrixes;

    private StringBuilder fieldContent = new StringBuilder("");


    public void fillBoard(int[][] matrix){
        synchronized(this) {
            int counter = 0;
            if (matrix.length != 7 || matrix[0].length != 3)
                return;
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        fields.get(counter).setFill(Color.web("WHITE"));
                    } else if (matrix[i][j] == 1) {
                        fields.get(counter).setFill(Color.web("#88fbc3"));
                    } else if (matrix[i][j] == 2) {
                        fields.get(counter).setFill(Color.web("#ffd167"));
                    } else
                        continue;
                    counter++;
                }
            }
        }
    }

    public int[][] getBoard(){
        int[][] board = new int[7][3];
        int counter = 0;
        for (int i = 0; i<7; i++){
            for(int j = 0; j < 3; j++){
                if(i == 3 && (j == 0 || j == 2)){
                    board[i][j] = -1;
                    continue;
                }
                Color c = (Color)fields.get(counter).getFill();
                if(c.equals(Color.web("WHITE")))
                    board[i][j] = 0;
                else if(c.equals(Color.web("#88fbc3")))
                    board[i][j] = 1;
                else if(c.equals(Color.web("#ffd167")))
                    board[i][j] = 2;
                counter++;
            }
        }
        return board;
    }


    public void wyswietlWiadomosc(String string){
        fieldContent.append(string).append("\n");
        textArea.setText(fieldContent.toString());
    }
}
