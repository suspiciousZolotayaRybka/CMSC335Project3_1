package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Main.java
 * Isaac Finehout
 * 13 April 2025
 *
 * This is the main class that runs the Project 3 program for CMSC 335.
 * @author fineh
 *
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Label hello = new Label("Hello World");
			BorderPane root = new BorderPane();
			root.setCenter(hello);
//TODO get lights, autopilot, and second window showing stats
			System.out.printf("IS CENTER: %s", root.isCenterShape());
//			System.out.printf("IS LEFT: %s", root.);
//			System.out.printf("IS RIGHT: %s", root.isCenterShape());
//			System.out.printf("IS UP: %s", root.isCenterShape());
//			System.out.printf("IS DOWN: %s", root.isCenterShape());

			Scene scene = new Scene(root, 1000, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
