package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    public static int FLAG = 0;
    Stage window;
    Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SceneChanger.fxml"));
        //Parent root2 = FXMLLoader.load(getClass().getResource("SceneChanger.fxml"));
        window = primaryStage;
        window.setTitle("Starry Instant Messenger");
        scene = new Scene(root, 600, 400);
        window.setScene(scene);
        window.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
