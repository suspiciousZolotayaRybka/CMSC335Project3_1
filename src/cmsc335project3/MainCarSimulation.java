package cmsc335project3;

import java.util.Arrays;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * MainCarSimulator.java
 * Isaac Finehout
 * 5 May 2025
 *
 * This is the main GUI implementation of the car simulator
 *
 * @author fineh
 *
 */

public class MainCarSimulation extends Application {

	// Class variables
	private Pane root;
	private Scene scene;
	private final CarSimulationManager carSimulationManager;

	// Class threads
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

		Button startButton;
		startButton = createButtonsAndReturnStartButton();
		// Add the start button, but no other buttons yet
		root.getChildren().add(startButton);

		Label[] informationLabels = new Label[2];
		informationLabels = createInformationLabels();
		// Add information labels
		root.getChildren().addAll(Arrays.asList((informationLabels)));

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

		scene = new Scene(root, 1600, 450);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/**
	 * Create the information labels for the project
	 *
	 * @return
	 */
	private Label[] createInformationLabels() {
		Label[] informationLabels = new Label[2];
		informationLabels[0] = new Label("Eastbound cars' y-coordinate = 160");
		informationLabels[0].setLayoutY(350);
		informationLabels[1] = new Label("Westbound cars' y-coordinate = 120");
		informationLabels[1].setLayoutY(380);

		for (Label l : informationLabels) {
			l.setLayoutX(30);
			l.setFont(Font.font("Roboto", 20));
		}
		return informationLabels;
	}

	/**
	 * Create the buttons for the program
	 */
	private Button createButtonsAndReturnStartButton() {
		// Initialize buttons
		Button startButton = new Button("Start New");
		startButton.setFont(Font.font("Roboto", 20));
		startButton.setLayoutY(275);
		startButton.setLayoutX(500);
		Button pauseButton = new Button("Pause");
		pauseButton.setFont(Font.font("Roboto", 20));
		pauseButton.setLayoutY(275);
		pauseButton.setLayoutX(500);
		Button continueButton = new Button("Continue");
		continueButton.setFont(Font.font("Roboto", 20));
		continueButton.setLayoutY(325);
		continueButton.setLayoutX(500);
		Button stopButton = new Button("Stop");
		stopButton.setFont(Font.font("Roboto", 20));
		stopButton.setLayoutY(375);
		stopButton.setLayoutX(500);

		// End the simulation, remove the buttons, and put a message thanking the user
		stopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// End the simulation and remove the buttons
				// Optionally, the user can start a new simulation by adding start button
				carSimulationManager.setSimulationRunning(false);
				root.getChildren().removeAll(pauseButton, continueButton, stopButton);
				Label endLabel = new Label("Simulation ended:\nThank you for using the Car Simulation Program.");
				endLabel.setFont(Font.font("Roboto", 20));
				endLabel.setTextFill(Color.RED);
				endLabel.setLayoutY(275);
				endLabel.setLayoutX(500);
				root.getChildren().add(endLabel);
			}
		});

		// Pause all threads
		pauseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				carSimulationManager.setPaused(true);
			}
		});

		// Continue all threads
		continueButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				carSimulationManager.setPaused(false);
			}
		});

		// The start button comes last, since it instantiates the other buttons on its
		// press
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
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

				// Remove the start button and add the other buttons
				root.getChildren().remove(startButton);
				root.getChildren().addAll(pauseButton, continueButton, stopButton);
			}
		});
		return startButton;
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