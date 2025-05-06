package cmsc335project3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

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
	private final Condition needEmptyCarSpace = carSimulationManagerLock.newCondition();
	private final Condition needAllCarsProduced = carSimulationManagerLock.newCondition();
	private boolean isSimulationRunning = true;
	private boolean isProducerProducing = true;
	private volatile boolean isPaused = false;
	private final Object pauseLock = new Object();

	// TODO change to better data structure
	private final CarSimulationClock carSimulationClock;
	private final MainCarSimulation mainCarSimulation;
	private final ArrayList<Car> cars;
	private final Road road = new Road(1600, new Point2D(0, 150));
	private final TrafficLight[] trafficLights;
	private int numberOfCarsVisibleOnScreen;

	/**
	 * Constructor for autopilot
	 */
	public CarSimulationManager(MainCarSimulation mainCarSimulation) {
		this.mainCarSimulation = mainCarSimulation;
		carSimulationClock = new CarSimulationClock(this);

		// TODO make simulation according to project requirements
		cars = this.createRandomizedCars();
//		cars = createTestingCars();// TODO save this and helper method for future debugging
		trafficLights = createTrafficLights();
//		trafficLights = createTestingTrafficLights(); // TODO delete save this and helper method for future debugging
		numberOfCarsVisibleOnScreen = 0;
	}

	/**
	 * Check to see if cars have collided and remove those that have
	 */
	public synchronized HashSet<Car> findCollidingCars() {
		// Create the collidingCars set
		HashSet<Car> collidingCars = new HashSet<>();

		try {
			carSimulationManagerLock.lock();

			// Check for collisions between unique pairs of cars
			for (int i = 0; i < cars.size(); i++) {
				// Get the first car to compare
				Car car_i = cars.get(i);
				if (!car_i.getIsInitializedOnScreen()) {
					// If the car is not initialized on the screen, skip this iteration
					continue;
				}

				for (int j = i + 1; j < cars.size(); j++) {
					// Only compare unique pairs, add 1 to i for starting j index
					Car car_j = cars.get(j);
					if (!car_j.getIsInitializedOnScreen()) {
						// If the car is not initialized on the screen, skip this iteration
						continue;
					}
					// If the cars collide, add it to the set
					if (car_i.collidesWithCar(car_j)) {
						collidingCars.add(car_i);
						collidingCars.add(car_j);
					}
				}
			}
		} finally {
			carSimulationManagerLock.unlock();
		}
		return collidingCars;
	}

	/**
	 * Update the clock
	 */
	public void updateCarSimulationClockText() {

		try {
			carSimulationManagerLock.lock();

			Platform.runLater(() -> {
				try {
					// Lock, get the new label, then continue running
					carSimulationManagerLock.lock();
					mainCarSimulation.getRoot().getChildren().remove(carSimulationClock.getLabelCarSimulationClock());

					Label label = new Label((new Date()).toString());
					label.setFont(Font.font("Roboto", 20));
					label.setLayoutX(30);
					label.setLayoutY(300);

					carSimulationClock.setLabelCarSimulationClock(label);

					mainCarSimulation.getRoot().getChildren().add(carSimulationClock.getLabelCarSimulationClock());
				} finally {
					carSimulationManagerLock.unlock();
				}
			});

		} finally {
			carSimulationManagerLock.unlock();
		}
	}

	/**
	 * This method moves the cars and checks for collision objects
	 */
	public void moveCars() {
		try {
			// Lock the manager lock and check if there are cars
			carSimulationManagerLock.lock();
			while (isProducerProducing) {
				needAllCarsProduced.await();
			}

			// This removes cars from the simulation if they collide
			// Future versions could implement live "crashes" instead of removing the cars
			try {
				carSimulationManagerLock.lock();
				removeCollidingCars(findCollidingCars());
			} finally {
				carSimulationManagerLock.unlock();
			}

			// The direction and speed of the car to move
			Direction direction;

			for (Car car : cars) {
				direction = car.getVelocityCar().getDirection();

				if (car.getIsInitializedOnScreen()) {
					// Set car to preferred velocity
					car.setVelocityCar(Velocity.from(car.getPreferredSpeed(), direction));

					// Find the car's predictedDeltaX
					double predictedDeltaX = findDeltaX(direction, car.getVelocityCar().getSpeed());

					// Set velocity to 0 MPH if it is not safe to move ahead
					if (!(findIsSafeToMoveAheadNoCarsInWay(car, direction, predictedDeltaX))) {
						car.setVelocityCar(Velocity.from(Speed.ZERO_MILES_PER_HOUR, direction));
					}
					if (!(findIsSafeToMoveAheadNoTrafficLightsInWay(car, direction))) {
						car.setVelocityCar(Velocity.from(Speed.ZERO_MILES_PER_HOUR, direction));
					}

					// Find the cars final deltaX
					double finalDeltaX = findDeltaX(direction, car.getVelocityCar().getSpeed());

					// Update the car's position
					car.setPositionCar(
							new Point2D(car.getPositionCar().getX() + finalDeltaX, car.getPositionCar().getY()));

					Platform.runLater(() -> {
						try {
							// Lock for the GUI update
							carSimulationManagerLock.lock();

							// Remove, update, then add the car back
							mainCarSimulation.getRoot().getChildren().remove(car.getCollisionShapeCar());
							mainCarSimulation.getRoot().getChildren().remove(car.getCarLabel());

							// Update car collision radiuses
							car.updateCollisionShapeCar();
							car.updateCollisionRadius();
							car.updateCollisionRadiusForTrafficLight();

							mainCarSimulation.getRoot().getChildren().add(car.getCollisionShapeCar());
							car.updateCarLabel();
							mainCarSimulation.getRoot().getChildren().add(car.getCarLabel());
						} finally {
							// Unlock the thread
							carSimulationManagerLock.unlock();
						}
					});
				}
			}

			// Check for and slow down cars in each others radius
			try {
				carSimulationManagerLock.lock();
//				slowDownCollisionRadiusCars(findCollisionRadiusCars());
			} finally {
				carSimulationManagerLock.unlock();
			}
		} catch (

		InterruptedException ie) {
			ie.printStackTrace();
		} finally {
			carSimulationManagerLock.unlock();
		}
	}

	/**
	 * DeltaX is the change in x positions of a car
	 *
	 * @param direction
	 * @param speedMilesPerHour
	 * @return
	 */
	public double findDeltaX(Direction direction, Speed speedMilesPerHour) {
		double speed = speedMilesPerHour.getMiles() / speedMilesPerHour.getHours();
		double deltaX = 0.35 * (direction == Direction.EAST ? speed : speed * -1);
		return deltaX;
	}

	/**
	 *
	 * @param car
	 * @param acceleration
	 * @return
	 */
	public boolean findIsSafeToMoveAheadNoCarsInWay(Car car, Direction direction, double deltaX) {
		double predictedX = car.getPositionCar().getX() + deltaX;
		Rectangle futureCarBounds = null;

		if (direction == Direction.EAST) {
			futureCarBounds = new Rectangle(predictedX, car.getPositionCar().getY(),
					car.getCollisionRadiusCar().getWidth(), car.getCollisionRadiusCar().getHeight());
		} else {
			// Car is moving WEST
			futureCarBounds = new Rectangle(predictedX - 110, car.getPositionCar().getY(),
					car.getCollisionRadiusCar().getWidth(), car.getCollisionRadiusCar().getHeight());
		}

		// Find the closest car ahead
		Car carAhead = null;
		double closestDistance = Double.MAX_VALUE;
		boolean isSafeToMoveNoCarsInWay = true;

		for (Car otherCar : cars) {
			// If the car is the same, continue
			if ((otherCar == car) || !otherCar.getIsInitializedOnScreen()) {
				continue;
			}

			Direction otherDirection = otherCar.getVelocityCar().getDirection();
			if (otherDirection != direction) {
				continue;
			} // Must be moving in same direction

			double dx = otherCar.getPositionCar().getX() - car.getPositionCar().getX();
			boolean isAhead = ((direction == Direction.EAST) && (dx > 0))
					|| ((direction == Direction.WEST) && (dx < 0));
			if (isAhead && (Math.abs(dx) < closestDistance)) {
				closestDistance = Math.abs(dx);
				carAhead = otherCar;
			}
		}

		if (carAhead != null) {
			// Cars are moving west
			Bounds carAheadBounds = carAhead.getCollisionRadiusCar().getBoundsInParent();
			if (futureCarBounds.getBoundsInParent().intersects(carAheadBounds)) {
				isSafeToMoveNoCarsInWay = false;
			}
		}
		return isSafeToMoveNoCarsInWay;
	}

	/**
	 * This detects traffic light collisions and updates the cars accordingly
	 */
	public boolean findIsSafeToMoveAheadNoTrafficLightsInWay(Car car, Direction direction) {

		boolean isSafeToMoveAheadNoTrafficLightsInWay = true;
		Bounds carTrafficLightBounds = car.getCollisionRadiusForTrafficLight().getBoundsInParent();

		// Loop over each traffic light with one car
		for (TrafficLight trafficLight : trafficLights) {

			// If the car is eastbound and is not colliding with an eastbound traffic light
			// or if the car is westbound and is not colliding with a westbound traffic
			// light continue the loop
			if (direction == Direction.EAST) {
				Bounds eastBoundTrafficLightBounds = trafficLight.getEastBoundCollisionRadiusTrafficLight()
						.getBoundsInParent();
				if (!(carTrafficLightBounds.intersects(eastBoundTrafficLightBounds))) {
					continue;
				}
			} else {
				Bounds westBoundTrafficLightBounds = trafficLight.getWestBoundCollisionRadiusTrafficLight()
						.getBoundsInParent();

				if (!(carTrafficLightBounds.intersects(westBoundTrafficLightBounds))) {
					continue;
				}
			}

			// If the loops continued all three times the car is not at a traffic light, and
			// the check below will skip

			// If the car is at a traffic light, check and see if it is green, yellow, or
			// red, and update accoringly
			if (trafficLight.getColorTrafficLight() == Color.GREEN) {
				isSafeToMoveAheadNoTrafficLightsInWay = true;
			} else {
				isSafeToMoveAheadNoTrafficLightsInWay = false;
			}
		}
		return isSafeToMoveAheadNoTrafficLightsInWay;
	}

	/**
	 *
	 * @param car the car to put in the simulation
	 */
	public void putCarsInSimulation() {

		try {
			carSimulationManagerLock.lock();
			while (numberOfCarsVisibleOnScreen >= cars.size()) {
				needEmptyCarSpace.await();
			}
			// Add the cars to the highway
			Platform.runLater(() -> {
				try {
					carSimulationManagerLock.lock();
					for (Car car : cars) {
						mainCarSimulation.getRoot().getChildren().add(car.getCollisionShapeCar());

						// Allow the moveCar method to move the car once unlocked
						car.setIsInitializedOnScreen(true);
						numberOfCarsVisibleOnScreen++;
					}

					if (carSimulationManagerLock.hasWaiters(needAllCarsProduced)
							&& (numberOfCarsVisibleOnScreen == cars.size())) {
						needAllCarsProduced.signal();
					}

				} finally {
					// Check for car collisions and remove any right away
					if (!(findCollidingCars().isEmpty())) {
						throw new IllegalArgumentException(
								"IllegalArgumentException in CarSimulationManager.java putCarsInSimulation() -> Cannot start from an ArrayList of cars where cars are already overlapping");
					}
					carSimulationManagerLock.unlock();
				}
			});
		} catch (

		InterruptedException ie) {
			System.out.println(
					"Interrupted Exception in CarSimulationManager.java, putCarInSimulationFullSpeed(Car car) method. Stack Trace below");
			ie.printStackTrace();
		} finally {
			carSimulationManagerLock.unlock();
		}

	}

	/**
	 *
	 * @param the collidingCars to be removed from the GUI
	 */
	public synchronized void removeCollidingCars(HashSet<Car> collidingCars) {

		if (!collidingCars.isEmpty()) {
			// If there are car collisions, remove the cars
			Platform.runLater(() -> {
				try {
					carSimulationManagerLock.lock();

					for (Car car : collidingCars) {
						mainCarSimulation.getRoot().getChildren().remove(car.getCollisionShapeCar());
						car.setIsInitializedOnScreen(false);
						numberOfCarsVisibleOnScreen--;

						if (numberOfCarsVisibleOnScreen == 0) {
							System.out.println("SIM OVER");
							isSimulationRunning = false;
						}
					}

				} finally {
					carSimulationManagerLock.unlock();
				}
			});
		}

	}

	public void takeCarFromSimulationAndDelete() {
		// TODO delete just to remove lastItem warning
//		delete this method?
	}

	/**
	 * Create and return the traffic lights
	 *
	 * @return the traffic lights to return
	 */
	public TrafficLight[] createTrafficLights() {
		TrafficLight[] trafficLights = new TrafficLight[] { new TrafficLight(new Point2D(300, 35), this, 10, 2, 12),
				new TrafficLight(new Point2D(800, 35), this, 10, 2, 12),
				new TrafficLight(new Point2D(1300, 35), this, 10, 2, 12) };
		return trafficLights;
	}

	/**
	 *
	 * @param trafficLight the traffic light to update
	 */
	public void updateTrafficLight(TrafficLight trafficLight) {
		try {
			carSimulationManagerLock.lock();
			// Find the traffic light color to switch to
			switch (trafficLight.getTlc()) {
			case GREEN -> trafficLight.setColorTrafficLight(Color.YELLOW);
			case YELLOW -> trafficLight.setColorTrafficLight(Color.RED);
			case RED -> trafficLight.setColorTrafficLight(Color.GREEN);
			}
			Platform.runLater(() -> {
				// Update the traffic light color
				mainCarSimulation.getRoot().getChildren().remove(trafficLight.getIndicatorTrafficLight());
				mainCarSimulation.getRoot().getChildren().add(trafficLight.getIndicatorTrafficLight());
			});

		} finally {
			carSimulationManagerLock.unlock();
		}
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
	 *
	 * @return the trafficLights to return
	 */
	public TrafficLight[] getTrafficLights() {
		return trafficLights;
	}

	/**
	 * @return the isSimulationRunning
	 */
	public boolean isSimulationRunning() {
		return isSimulationRunning;
	}

	/**
	 * @return the isProducerProducing
	 */
	public boolean isProducerProducing() {
		return isProducerProducing;
	}

	/**
	 * @param isSimulationRunning the isSimulationRunning to set
	 */
	public void setSimulationRunning(boolean isSimulationRunning) {
		this.isSimulationRunning = isSimulationRunning;
	}

	/**
	 *
	 * @param the isProducerProducing to set
	 */
	public void setIsProducerProducing(boolean isProducerProducing) {
		this.isProducerProducing = isProducerProducing;
	}

	/**
	 *
	 * @return
	 */
	private ArrayList<Car> createRandomizedCars() {

		ArrayList<Car> randomizedCars = new ArrayList<Car>();

		try {
			carSimulationManagerLock.lock();
			Random random = new Random();
			Velocity velocity = null;
			Point2D point2D = null;
			Color color = null;

			ArrayList<Double> eastBoundCarXValues = new ArrayList<Double>();
			ArrayList<Double> westBoundCarXValues = new ArrayList<Double>();
			// Determine the x and y values for east and west bound car traffic
			double eastBoundCarPlacement = -7000;
			double westBoundCarPlacement = 8000;
			while (eastBoundCarPlacement < -100) {
				// Car is 75 pixels, add 160 to prevent pixels from causing an
				// IllegalArgumentException for overlapping cars
				eastBoundCarPlacement += random.nextDouble(160, 200);
				eastBoundCarXValues.add(eastBoundCarPlacement);
			}
			while (westBoundCarPlacement > 1100) {
				westBoundCarPlacement -= random.nextDouble(160, 200);
				westBoundCarXValues.add(westBoundCarPlacement);
			}

			// Add EastBound cars
			for (int i = 0; i < eastBoundCarXValues.size(); i++) {
				point2D = new Point2D(eastBoundCarXValues.get(i), 160);
				color = new Color(random.nextDouble(1.0), random.nextDouble(1.0), random.nextDouble(1.0), 1.0);

				// Find a random velocity
				switch (random.nextInt(3)) {
				case 0 -> velocity = Velocity.EAST_FIVE_MPH;
				case 1 -> velocity = Velocity.EAST_TEN_MPH;
				case 2 -> velocity = Velocity.EAST_FIFTEEN_MPH;
				}

				// Add the finished car
				randomizedCars.add(new Car(point2D, color, velocity, this));
			}

			// Add WestBound cars
			for (int i = 0; i < westBoundCarXValues.size(); i++) {
				point2D = new Point2D(westBoundCarXValues.get(i), 120);
				color = new Color(random.nextDouble(1.0), random.nextDouble(1.0), random.nextDouble(1.0), 1.0);

				// Find a random velocity
				switch (random.nextInt(3)) {
				case 0 -> velocity = Velocity.WEST_FIVE_MPH;
				case 1 -> velocity = Velocity.WEST_TEN_MPH;
				case 2 -> velocity = Velocity.WEST_FIFTEEN_MPH;
				}

				// Add the finished car
				randomizedCars.add(new Car(point2D, color, velocity, this));
			}

		} finally {
			carSimulationManagerLock.unlock();
		}
		return randomizedCars;
	}

	public void setPaused(boolean paused) {
		isPaused = paused;
		if (!isPaused) {
			synchronized (pauseLock) {
				pauseLock.notifyAll();
			}
		}
	}

	public boolean isPaused() {
		return isPaused;
	}

	public Object getPauseLock() {
		return pauseLock;
	}

}