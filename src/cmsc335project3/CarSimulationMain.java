package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * CarSimulationMain.java
 * Isaac Finehout
 * 13 April 2025
 *
 * This is the main class that runs the Project 3 program for CMSC 335.
 * @author fineh
 *
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CarSimulationMain extends Application {

	private Pane root;
	private Scene scene;
	private final CarSimulationManager carSimulationManager;

	public CarSimulationMain() {
		// TODO Before making the manager, add customer requirements
		carSimulationManager = new CarSimulationManager(new TestingSpace()); // TODO change to this
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			root.getChildren().add(carSimulationManager.getRoad().getCollisionShapeRoad());

//TODO get lights, autopilot, and second window showing stats
// TODO create producers, consumers, and then use autopilot as the passive to manage threads
// TODO create initial popup requesting how many cars and which directions, along with seconds between each car entering the screen. Maybe more?

			scene = new Scene(root, 1000, 400);
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
