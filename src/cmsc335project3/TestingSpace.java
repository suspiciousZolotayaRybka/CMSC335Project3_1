package cmsc335project3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestingSpace extends Application {

	private Pane root;
	private Scene scene;
	private final CarSimulationManager carSimulationManager;
	private Thread carProducerThread;
	private Thread carMoverThread;

	public TestingSpace() {
		carSimulationManager = new CarSimulationManager(this);
	}

	@Override
	public void start(Stage primaryStage) {

		root = new Pane();
		root.getChildren().add(carSimulationManager.getRoad().getCollisionShapeRoad());
		scene = new Scene(root, 1000, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

		// Start and name threads TODO delete rename after done debugging?
		carProducerThread = new Thread(new CarProducer(carSimulationManager));
		carMoverThread = new Thread(new CarMover(carSimulationManager));
		carProducerThread.setName("carProducerThread");
		carMoverThread.setName("carMoverThread");
		carProducerThread.start();
		carMoverThread.start();
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

	/**
	 * @return the carMoverThread
	 */
	public Thread getCarMoverThread() {
		return carMoverThread;
	}

	public static void main(String[] args) {
		launch(args);
	}
}