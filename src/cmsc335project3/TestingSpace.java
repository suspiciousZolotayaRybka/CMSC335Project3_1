package cmsc335project3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestingSpace extends Application {

	private Pane root;
	private Scene scene;
	private final CarSimulationManager carSimulationManager;

	public TestingSpace() {
		carSimulationManager = new CarSimulationManager(this);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			root.getChildren().add(carSimulationManager.getRoad().getCollisionShapeRoad());
			scene = new Scene(root, 1000, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			(new Thread(new CarProducer(carSimulationManager))).start();
			(new Thread(new CarMover(carSimulationManager))).start();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the root
	 */
	public Pane getRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(Pane root) {
		this.root = root;
	}

	public static void main(String[] args) {
		launch(args);
	}
}