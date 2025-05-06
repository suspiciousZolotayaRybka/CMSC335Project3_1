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

public class MainCarSimulation extends Application {

	private Pane root;
	private Scene scene;
	private final CarSimulationManager carSimulationManager;
	private Thread carProducerThread;
	private Thread carMoverThread;
	private Thread trafficLightThread_0;
	private Thread trafficLightThread_1;
	private Thread trafficLightThread_2;
	private Thread carSimulationClockThread;

	public MainCarSimulation() {
		carSimulationManager = new CarSimulationManager(this);
	}

	@Override
	public void start(Stage primaryStage) {

		root = new Pane();

		// Add children to the pane
		root.getChildren().addAll(carSimulationManager.getRoad().getCollisionShapeRoad(),
				carSimulationManager.getTrafficLights()[0].getEastBoundCollisionRadiusTrafficLight(),
				carSimulationManager.getTrafficLights()[1].getEastBoundCollisionRadiusTrafficLight(),
				carSimulationManager.getTrafficLights()[2].getEastBoundCollisionRadiusTrafficLight(),
				carSimulationManager.getTrafficLights()[0].getWestBoundCollisionRadiusTrafficLight(),
				carSimulationManager.getTrafficLights()[1].getWestBoundCollisionRadiusTrafficLight(),
				carSimulationManager.getTrafficLights()[2].getWestBoundCollisionRadiusTrafficLight(),
				carSimulationManager.getTrafficLights()[0].getIndicatorTrafficLight(),
				carSimulationManager.getTrafficLights()[1].getIndicatorTrafficLight(),
				carSimulationManager.getTrafficLights()[2].getIndicatorTrafficLight());

		scene = new Scene(root, 1600, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

		// Start Threads
		carProducerThread = new Thread(new CarProducer(carSimulationManager));
		carMoverThread = new Thread(new CarMover(carSimulationManager));
		carSimulationClockThread = new Thread(new CarSimulationClock(carSimulationManager));
		carProducerThread.setName("carProducerThread");
		carMoverThread.setName("carMoverThread");
		carSimulationClockThread.setName("carSimulationClockThread");
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
		carSimulationClockThread.start();

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