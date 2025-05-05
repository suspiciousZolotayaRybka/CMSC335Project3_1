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
	private Thread trafficLightThread_0;
	private Thread trafficLightThread_1;
	private Thread trafficLightThread_2;

	public TestingSpace() {
		carSimulationManager = new CarSimulationManager(this);
	}

	@Override
	public void start(Stage primaryStage) {

		root = new Pane();
		root.getChildren().addAll(carSimulationManager.getRoad().getCollisionShapeRoad(),
				carSimulationManager.getTrafficLights()[0].getIndicatorTrafficLight(),
				carSimulationManager.getTrafficLights()[1].getIndicatorTrafficLight(),
				carSimulationManager.getTrafficLights()[2].getIndicatorTrafficLight());
// TODO delete
		// root.getChildren().addAll(carSimulationManager.getRoad().getCollisionShapeRoad(),
//				carSimulationManager.getTrafficLights()[0].getCollisionRadiusTrafficLight(),
//				carSimulationManager.getTrafficLights()[1].getCollisionRadiusTrafficLight(),
//				carSimulationManager.getTrafficLights()[2].getCollisionRadiusTrafficLight(),
//				carSimulationManager.getTrafficLights()[0].getIndicatorTrafficLight(),
//				carSimulationManager.getTrafficLights()[1].getIndicatorTrafficLight(),
//				carSimulationManager.getTrafficLights()[2].getIndicatorTrafficLight());
		// TODO delete

		scene = new Scene(root, 1000, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

		// Start and name threads TODO delete rename after done debugging?
		carProducerThread = new Thread(new CarProducer(carSimulationManager));
		carMoverThread = new Thread(new CarMover(carSimulationManager));
		carProducerThread.setName("carProducerThread");
		carMoverThread.setName("carMoverThread");
		TrafficLight[] trafficLights = carSimulationManager.getTrafficLights();
		trafficLightThread_0 = new Thread(trafficLights[0]);
		trafficLightThread_1 = new Thread(trafficLights[1]);
		trafficLightThread_2 = new Thread(trafficLights[2]);
		trafficLightThread_0.setName("trafficLightThread_0");
		trafficLightThread_1.setName("trafficLightThread_1");
		trafficLightThread_2.setName("trafficLightThread_2");

		carProducerThread.start();
		carMoverThread.start();
		trafficLightThread_0.start();
		trafficLightThread_1.start();
		trafficLightThread_2.start();

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