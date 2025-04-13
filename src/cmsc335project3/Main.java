package cmsc335project3;

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

			System.out.printf("IS CENTER: %s", root.isCenterShape());
//			System.out.printf("IS LEFT: %s", root.);
//			System.out.printf("IS RIGHT: %s", root.isCenterShape());
//			System.out.printf("IS UP: %s", root.isCenterShape());
//			System.out.printf("IS DOWN: %s", root.isCenterShape());

			Scene scene = new Scene(root, 400, 400);
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
