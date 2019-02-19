package edu.uprb.chat.controller;

import edu.uprb.chat.ChatClient;
import edu.uprb.chat.ChatServer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class SignInController {
    Stage window;
    Scene scene1,scene2;
    AnchorPane chatLayout;
    VBox chatLayout2;


    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField signInEmail;

    @FXML
    private PasswordField signInPassword;

    @FXML
    private Button btnSignIn;

    @FXML
    private Button btnRegister;

    @FXML
    private Label invalidMessage;

    public void btnSignIn(){
        String userEmail = signInEmail.getText();
        String userPassword = signInPassword.getText();
        boolean flag = false;

        try {
            FileReader in = new FileReader("C:\\Users\\ASUS\\IdeaProjects\\FXML-example\\src\\edu\\uprb\\chat\\userRecord.txt");
            BufferedReader br = new BufferedReader(in);

            while (br.readLine() != null) {
                String str = br.readLine();
                System.out.println(str);
                String[] split = str.split("<<");
                if(split[0].equals(userEmail) && split[1].equals(userPassword)){
                    flag = true;
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!flag){
            invalidMessage.setText("Invalid Login");
        }else {
            window = ChatClient.primaryStage;


            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ChatClient.class.getResource("view/ClientGUI.fxml"));
                loader.setController(new ClientController());
                chatLayout2 = (VBox) loader.load();

                // Show the scene containing the root layout.
                Scene scene = new Scene(chatLayout2);
                window.setScene(scene);
                window.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void btnRegister(javafx.event.ActionEvent actionEvent){
        //ChatClient cc = new ChatClient();
        window = ChatClient.primaryStage;


        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ChatServer.class.getResource("view/css/Register.fxml"));
            chatLayout = (AnchorPane) loader.load();
// Show the scene containing the root layout.
            Scene scene = new Scene(chatLayout);
            window.setScene(scene);
            window.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}