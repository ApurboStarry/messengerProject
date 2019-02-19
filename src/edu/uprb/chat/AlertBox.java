package edu.uprb.chat;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {

    public static void display() {
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Registration Status");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText("Registration Complete");
        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> {
            window.close();
            ChatClient.initChatLayout();
        });

        VBox layout = new VBox(20);
        layout.setMinSize(60,60);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        //closeButton.setOnAction(e -> ChatClient.initChatLayout());
    }

}