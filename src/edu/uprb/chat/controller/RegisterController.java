package edu.uprb.chat.controller;

import edu.uprb.chat.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RegisterController {

    @FXML
    private TextField registerEmail;

    @FXML
    private TextField registerPassword;

    @FXML
    private TextField confirmPassword;

    @FXML
    private Label errorMessage;

    public void registerFinally(){
        try {
            if(!registerPassword.getText().equals(confirmPassword.getText())){
                errorMessage.setText("Retype password.");
            }else {
                File file = new File("C:\\Users\\ASUS\\IdeaProjects\\FXML-example\\src\\edu\\uprb\\chat\\userRecord.txt");
                FileWriter writer = new FileWriter(file, true);
                PrintWriter output = new PrintWriter(writer);
                output.println(registerEmail.getText() + "<<" + registerPassword.getText());
                writer = new FileWriter(file, true);
                output.close();
                AlertBox.display();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
