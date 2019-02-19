package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    @FXML
    private Label firstLabel = new Label();

    @FXML
    private TextField loggedInAs = new TextField();

    @FXML
    private TextField clientMesseges = new TextField();


    public void setLoggedInAs(SceneChangerController obj){
        loggedInAs.setText(obj.userNameInput.getText());
    }

    public String fromUser(){
        String in = clientMesseges.getText();
        return in;
    }

    public void print(){
        while(!fromUser().equals("Over")){
            String in = fromUser();
            System.out.println(in);
            clientMesseges.setText("");
        }
    }
}
