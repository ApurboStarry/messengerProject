package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SceneChangerController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    public TextField userNameInput = new TextField();

    @FXML
    private PasswordField passwordInput = new PasswordField();

    @FXML
    private Label initializeMessage1;

    @FXML
    private Label initializeMessage2;

    @FXML
    private Label initializeMessage3;

    @FXML
    private Label loginErrorMessage;

    @FXML
    private TextField loggedInAs = new TextField();


    public void loadSecondScene(javafx.event.ActionEvent actionEvent) throws IOException {
        String inp = userNameInput.getText();
        if(inp.equals("Apurbo") && passwordInput.getText().equals("1605082")){
            //AnchorPane pane = FXMLLoader.load(getClass().getResource("sample.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent pane = loader.load();
            //loggedInAs.setText(userNameInput.getText());
            Controller displayLoggedInAs = loader.getController();
            String in = userNameInput.getText();
            displayLoggedInAs.setLoggedInAs(this);
            System.out.println(inp + " " + passwordInput.getText());
            rootPane.getChildren().setAll(pane);
            //secondLabel.textProperty().bind(userNameInput.textProperty());
            Main.FLAG = 1;
        }else{
            loginErrorMessage.setText("Invalid Login");
        }
    }
}
