package sample;

import imageRecognition.ImageGrabber;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Thread gameThread;
    private ImageGrabber imgGrabber;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            imgGrabber.close();
            gameThread.interrupt();
            try {
                gameThread.join();
            }
            catch(Exception ignored){}
        });

        imgGrabber = new ImageGrabber("test");
        gameThread = new Thread(new Rules(loader.getController()));

        Thread grabberThread = new Thread(imgGrabber);

        grabberThread.start();
        Thread.sleep(2000);
        gameThread.start();
    }



    public static void main(String[] args) {
        launch(args);

    }
}
