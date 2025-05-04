package cmsc335project3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * CarSimulationManager.java
 * Isaac Finehout
 * 23 April 2025
 *
 * This is the passive object that controls all active objects
 * @author fineh
 *
 */

public class CarSimulationManager {

	// Concurrency items
	private final ReentrantLock carSimulationManagerLock = new ReentrantLock();
//	private final Condition carOnScreen = carSimulationManagerLock.newCondition(); TODO delete?
	private final Condition needCar = carSimulationManagerLock.newCondition();
	private final Condition needEmptyCarSpace = carSimulationManagerLock.newCondition();
	private boolean isSimulationRunning = true;

	// TODO change to better data structure
	private final TestingSpace testingSpace; // TODO change to main
	private final ArrayList<Car> cars;
	private final Road road = new Road(1000, new Point2D(0, 150));
	private int lastItem;
	private int numberOfItems;

	/**
	 * Constructor for autopilot
	 */
	public CarSimulationManager(TestingSpace testingSpace) {
		this.testingSpace = testingSpace;

		// TODO make simulation according to customer requirements
		cars = this.createRandomizedCars();
		numberOfItems = 0;
		lastItem = 0;
	}

	public void moveCars() {
		try {
			// Lock the manager lock and check if there are cars
			carSimulationManagerLock.lock();
			while (numberOfItems == 0) {
				needCar.await();
			}

			// The direction and speed of the car to move
			Direction direction;
			double speedMilesPerHour;

			for (Car car : cars) {

				if (car.getIsInitializedOnScreen()) {
					// Update the car location based on direction and speed
					direction = car.getVelocityCar().getDirection();
					speedMilesPerHour = car.getVelocityCar().getSpeed().getMiles()
							/ car.getVelocityCar().getSpeed().getHours();

				//@formatter:off
				double acceleration = 0.1 *
						(direction == Direction.EAST ?
								speedMilesPerHour :
									speedMilesPerHour * -1);
				//@formatter:on
					car.setPositionCar(
							new Point2D(car.getPositionCar().getX() + acceleration, car.getPositionCar().getY()));
					Platform.runLater(() -> {
						// Remove, update, then add the car back
						testingSpace.getRoot().getChildren().remove(car.getCollisionShapeCar());
						car.updateCollisionShapeCar();
						testingSpace.getRoot().getChildren().add(car.getCollisionShapeCar());

						// Check for collisions with other cars, and make them explode
						for (Car car_j : cars) {
							if (car != car_j) {
								Shape intersection = Shape.intersect(car.getCollisionShapeCar(),
										car_j.getCollisionShapeCar());
								if ((intersection.getBoundsInLocal().getWidth() > 0)
										&& (intersection.getBoundsInLocal().getHeight() > 0)) {
									System.out.println("BOOM");
									testingSpace.getRoot().getChildren().remove(car.getCollisionShapeCar());
									testingSpace.getRoot().getChildren().remove(car_j.getCollisionShapeCar());
									cars.remove(car);
									cars.remove(car_j);
									break;// TODO
								}
							}
						}

					});
				}
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} finally {
			carSimulationManagerLock.unlock();
		}
	}

	/**
	 *
	 * @param car the car to put in the simulation
	 */
	public void putCarInSimulation(Car car) {

		try {
			carSimulationManagerLock.lock();
			while (numberOfItems >= cars.size()) {
				needEmptyCarSpace.await();
			}

			// Add the car to the highway
			Platform.runLater(() -> {
				System.out.println(
						"CarSimulatorManagerCriticalSection:Car placed in sim at: " + car.getPositionCar().toString());
				testingSpace.getRoot().getChildren().add(car.getCollisionShapeCar());

			});
			lastItem = (++lastItem % cars.size());
			numberOfItems++;

			if (carSimulationManagerLock.hasWaiters(needCar)) {
				needCar.signal();
			}
			// Allow the moveCar method to move the car
			car.setIsInitializedOnScreen(true);
		} catch (InterruptedException ie) {
			System.out.println(
					"Interrupted Exception in CarSimulationManager.java, putCarInSimulationFullSpeed(Car car) method. Stack Trace below");
			ie.printStackTrace();
		} finally {
			carSimulationManagerLock.unlock();
		}

	}

	public void takeCarFromSimulationAndDelete() {
		// TODO delete just to remove lastItem warning
		System.out.println(lastItem);
	}

	public void updatePane(Pane root) {

		try {
			carSimulationManagerLock.lock();
//			while (numberOfItems >= cars.size()) {
//				needEmptyCarSpace.await();
//			} TODO while method to check for race condition? or does TestingSpace.run cover it?

			root.getChildren().clear();
			root.getChildren().addAll(road.getCollisionShapeRoad(), cars.get(0).getCollisionShapeCar());
			// TODO test for changes in objects and don;'t update if no changes

//			if (carSimulationManagerLock.hasWaiters(needCar)) {
//				needCar.signal();
//			}

		} catch (Exception e) { // TODO see if InterruptedException specific?
			System.out.println("Exception in CarSimulationManager.java, updatePane() method. Stack Trace below");
			e.printStackTrace();
		} finally {
			carSimulationManagerLock.unlock();
		}

	}

	/**
	 * @return the carSimulationManagerLock
	 */
	public ReentrantLock getcarSimulationManagerLock() {
		return carSimulationManagerLock;
	}

	/**
	 * @return the cars
	 */
	public ArrayList<Car> getCars() {
		return cars;
	}

	/**
	 * @return the road
	 */
	public Road getRoad() {
		return road;
	}

	/**
	 * @return the isSimulationRunning
	 */
	public boolean isSimulationRunning() {
		return isSimulationRunning;
	}

	/**
	 * @param isSimulationRunning the isSimulationRunning to set
	 */
	public void setSimulationRunning(boolean isSimulationRunning) {
		this.isSimulationRunning = isSimulationRunning;
	}

	/**
	 *
	 * @return
	 */
	private ArrayList<Car> createRandomizedCars() {
		Random random = new Random();
		ArrayList<Car> cars = new ArrayList<Car>();
		Velocity velocity = null;
		Direction direction = null;
		Speed speed = null;
		Point2D point2D = null;
		Color color = null;
		Car car = null;
		int numCars = random.nextInt(3, 8);

		for (int i = 0; i < numCars; i++) {

			if (random.nextBoolean()) {
				direction = Direction.EAST;
			} else {
				direction = Direction.WEST;
			}

			switch (random.nextInt(3)) {
			case 0 -> velocity = (direction == Direction.EAST ? Velocity.EAST_FIVE_MPH : Velocity.WEST_FIVE_MPH);
			case 1 -> velocity = (direction == Direction.EAST ? Velocity.EAST_TEN_MPH : Velocity.WEST_TEN_MPH);
			case 2 -> velocity = (direction == Direction.EAST ? Velocity.EAST_FIFTEEN_MPH : Velocity.WEST_FIFTEEN_MPH);
			}

			point2D = new Point2D(random.nextDouble(100, 900), direction == Direction.EAST ? 160 : 120);

			color = new Color(random.nextDouble(1.0), random.nextDouble(1.0), random.nextDouble(1.0), 1.0);

			cars.add(new Car(point2D, color, velocity));
		}

		return cars;
	}

}