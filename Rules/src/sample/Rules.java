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

    int[][] matrix_camera = test.matrix4;
    int[][] matrix_board = test.matrix;

    public ArrayList<Integer> checkDifferences() {

        ArrayList<Integer> change = new ArrayList<>();

        for (int i = 0; i < matrix_camera.length; i++) {
            for (int j = 0; j < matrix_camera[0].length; j++) {
                if (matrix_camera[i][j] != matrix_board[i][j]) {
                    change.add(i);
                    change.add(j);
                }
            }
        }

        return change;
    }

    public Boolean checkMove(ArrayList<Integer> change) {

        if (change.size() == 4) {
            int x1 = change.get(0);
            int y1 = change.get(1);
            int x2 = change.get(2);
            int y2 = change.get(3);

            if (checkLength(x1, y1, x2, y2) == 1) {
                return true;
            }
        }
        return false;
    }

    public Boolean checkBicie(ArrayList<Integer> change) {
        if (change.size() == 6) {
            int x1 = change.get(0);
            int y1 = change.get(1);
            int x2 = change.get(2);
            int y2 = change.get(3);
            int x3 = change.get(4);
            int y3 = change.get(5);
            if (matrix_camera[x2][y2] != 0)         //w przypadku bicia 'środkowe' pole zawsze będzie puste
                return false;
            else {
                if (matrix_camera[x1][y1] == 0 && matrix_camera[x3][y3] == 0)
                    return false;      //wszystkie pola nie mogą być puste
                else {
                    return matrix_camera[x1][y1] != matrix_board[x1][y1] && matrix_camera[x3][y3] != matrix_board[x3][y3];
                }
            }
        } else
            return false;
    }

    public int checkLength(int x1, int y1, int x2, int y2) {
        if (x1 == 3 || x2 == 3) {
            return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
        } else {
            if (x1 == x2) {
                return Math.abs(y1 - y2);
            } else if (y1 == y2) {
                return Math.abs(x1 - x2);
            } else return 0;
        }
    }

    public int getFieldNumber(int y, int x) {
        return (3 * y + x);
    }

    @Override
    public void run() {
        Game game = new Game(new Player(PlayerType.Human, 1), new Player(PlayerType.Human, 2), kontroler);
        game.start(matrix_board);
        while (true) {
            try {
                Thread.sleep(1000);
                matrix_camera = test.matrix5;
                ArrayList<Integer> change = checkDifferences();
                Boolean ifMove = checkMove(change);
                Boolean ifBicie = checkBicie(change);
                if (ifMove && !game.getCaptureRequired()) {
                    int pole1 = getFieldNumber(change.get(0), change.get(1));
                    int pole2 = getFieldNumber(change.get(2), change.get(3));
                    if (matrix_camera[change.get(0)][change.get(1)] != 0) {
                        int kolor = matrix_camera[change.get(0)][change.get(1)];
                        if (matrix_board[change.get(0)][change.get(1)] != kolor) {
                            game.setNewMove(new Pair<>(pole2, pole1));
                            kontroler.wyswietlWiadomosc("Wykonano ruch z pola" + pole2 + "na pole" + pole1);
                        }
                    } else {
                        game.setNewMove(new Pair<>(pole1, pole2));
                        kontroler.wyswietlWiadomosc("Wykonano ruch z pola" + pole1 + "na pole" + pole2);
                    }
                }
                if (ifBicie && game.getCaptureRequired()) {
                    int poczatkowe = -1;
                    int koncowe = -1;
                    int pole1 = getFieldNumber(change.get(0), change.get(1));
                    int pole2 = getFieldNumber(change.get(2), change.get(3));
                    int pole3 = getFieldNumber(change.get(4), change.get(5));
                    if (matrix_camera[change.get(0)][change.get(1)] != 0) {
                        koncowe = pole1;
                        int kolor = matrix_camera[change.get(0)][change.get(1)];
                        if (matrix_board[change.get(2)][change.get(3)] == kolor) {
                            poczatkowe = pole2;
                        } else if (matrix_board[change.get(4)][change.get(5)] == kolor){
                            poczatkowe = pole3;
                        }
                    } else if (matrix_camera[change.get(4)][change.get(5)] != 0) {
                        koncowe = pole3;
                        int kolor = matrix_camera[change.get(4)][change.get(5)];
                        if (matrix_board[change.get(0)][change.get(1)] == kolor) {
                            koncowe = pole1;
                        }
                        else if (matrix_board[change.get(2)][change.get(3)] == kolor) {
                            koncowe = pole2;
                        }

                    } else if (matrix_camera[change.get(2)][change.get(3)] != 0 ) {
                        koncowe = pole2;
                        int kolor = matrix_camera[change.get(2)][change.get(3)];
                        if(matrix_board[change.get(0)][change.get(1)] == kolor){
                            koncowe = pole1;
                        }
                        else if(matrix_board[change.get(4)][change.get(5)] == kolor){
                            koncowe = pole3;
                        }
                    }
                    if(poczatkowe != -1 && koncowe != -1){
                        game.setNewMove(new Pair<>(poczatkowe, koncowe));
                        kontroler.wyswietlWiadomosc("Wykonano bicie z pola " + poczatkowe + " na pole " + koncowe);
                    }
                }

                matrix_board = game.getMatrix();

            } catch (InterruptedException e) {
                game.stop();
                break;
            }
        }
    }
}
