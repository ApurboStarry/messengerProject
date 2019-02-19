package edu.uprb.chat;

import java.io.IOException;

import edu.uprb.chat.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatClient extends Application {

	public static Stage primaryStage;
	private static AnchorPane chatLayout;
	Scene scene1,scene2;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Data Communication Chat App");

		ChatClient.initChatLayout();
	}

	public static void initChatLayout() {
		try {
			/*
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ChatClient.class.getResource("view/ClientGUI.fxml"));
			loader.setController(new ClientController());
			chatLayout = (VBox) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(chatLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			*/
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ChatServer.class.getResource("view/css/SignIn.fxml"));
			chatLayout = (AnchorPane) loader.load();
// Show the scene containing the root layout.
			Scene scene = new Scene(chatLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
