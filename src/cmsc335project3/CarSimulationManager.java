package cmsc335project3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

//TODO 75 pixels car length DELETE

public class CarSimulationManager {

	// Concurrency items
	private final ReentrantLock carSimulationManagerLock = new ReentrantLock();
	private final Condition needEmptyCarSpace = carSimulationManagerLock.newCondition();
	private final Condition needAllCarsProduced = carSimulationManagerLock.newCondition();
	private boolean isSimulationRunning = true;
	private boolean isProducerProducing = true;

	// TODO change to better data structure
	private final TestingSpace testingSpace; // TODO change to main
	private final ArrayList<Car> cars;
	private final Road road = new Road(1000, new Point2D(0, 150));
	private final TrafficLight[] trafficLights;
	private int numberOfCarsVisibleOnScreen;

	/**
	 * Constructor for autopilot
	 */
	public CarSimulationManager(TestingSpace testingSpace) {
		this.testingSpace = testingSpace;

		// TODO make simulation according to project requirements
		cars = this.createRandomizedCars();
//		cars = createTestingCars();// TODO delete
//		trafficLights = createTrafficLights();
		trafficLights = createTestingTrafficLights(); // TODO delete
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
					if (car_i.collidesWith(car_j)) {
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
	 * Check to see if cars are in one another's radius and slow those going faster
	 * behind others
	 */
	public synchronized void findCollisionRadiusCarsAndSlowCarsBehindOthers() {
		try {
			carSimulationManagerLock.lock();
			// Check for cars approaching other cars
			Car car_i;
			boolean isBehindAnotherCarRadius = false;
			Rectangle collisionRadius_i;
			Rectangle collisionRadius_j;

			// Use a nested for loop to check each car against every other car
			for (int i = 0; i < cars.size(); i++) {
				car_i = cars.get(i);
				collisionRadius_i = cars.get(i).getCollisionRadiusCar();
				for (int j = i + 1; j < cars.size(); j++) {
					// Only compare unique pairs, add 1 to i for starting j index
					collisionRadius_j = cars.get(j).getCollisionRadiusCar();
					if (car_i.getVelocityCar().getDirection() == Direction.EAST) {
						// If the car is eastbound, check to see if a car is in front of its collision
						// radius
						if (car_i.isWithinCollisionRadiusCar(collisionRadius_j)) {
							// Only slow the car down if it is behind another car
							// If the cars are moving east, this means the differences of x will be negative
							if (((collisionRadius_i.getX()) - (collisionRadius_j.getX())) < 0) {
								// slow down the car
								isBehindAnotherCarRadius = true;
								car_i.setVelocityCar(Velocity.EAST_FIVE_MPH);
							}
						}
					} else {
						// if the car is going westbound, check to see if a car is west in front of its
						// collision radius
						if (car_i.isWithinCollisionRadiusCar(collisionRadius_j)) {
							// Only slow the car down if it is behind another car
							// If the cars are moving west, this means the differences of x will be positive
							if (((collisionRadius_i.getX()) - (collisionRadius_j.getX())) > 0) {
								// slow down the car
								isBehindAnotherCarRadius = true;
								car_i.setVelocityCar(Velocity.WEST_FIVE_MPH);
							}
						}
					}

					// If the car is not behind another car, speed it up!
					if (!isBehindAnotherCarRadius) {
						Velocity newVelocity = Velocity.from(car_i.getPreferredSpeed(),
								car_i.getVelocityCar().getDirection());
						car_i.setVelocityCar(newVelocity);
					}
				}
			}

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
				double acceleration = 0.35 *
						(direction == Direction.EAST ?
								speedMilesPerHour :
									speedMilesPerHour * -1);
				//@formatter:on
					car.setPositionCar(
							new Point2D(car.getPositionCar().getX() + acceleration, car.getPositionCar().getY()));
					Platform.runLater(() -> {
						try {
							// Lock for the GUI update
							carSimulationManagerLock.lock();
							// Remove, update, then add the car back
							testingSpace.getRoot().getChildren().remove(car.getCollisionShapeCar());
							car.updateCollisionShapeCar();
							car.updateCollisionRadiuses();
							testingSpace.getRoot().getChildren().add(car.getCollisionShapeCar());
						} finally {
							// Unlock the thread
							carSimulationManagerLock.unlock();
						}
					});
				}
			}
			// Check for and remove car collisions
			// Slow cars moving faster behind others
			try {
				carSimulationManagerLock.lock();
				findCollisionRadiusCarsAndSlowCarsBehindOthers();
//				removeCollidingCars(findCollidingCars());
			} finally {
				carSimulationManagerLock.unlock();
			}

			// Check for and slow down cars in each others radius
			try {
				carSimulationManagerLock.lock();
//				slowDownCollisionRadiusCars(findCollisionRadiusCars());
			} finally {
				carSimulationManagerLock.unlock();
			}
			// TODO delete
			System.out.printf("cars.size()=%d%n,numberOfCarsVisibleOnScreen=%d%n", cars.size(),
					numberOfCarsVisibleOnScreen);
			for (int i = 0; i < cars.size(); i++) {
				if (cars.get(i).getIsInitializedOnScreen()) {
					System.out.printf("%s%n", cars.get(i).toString());
				}
			}
			System.out.println("\n======\n");
// TODO delete
		} catch (

		InterruptedException ie) {
			ie.printStackTrace();
		} finally {
			carSimulationManagerLock.unlock();
		}
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
						// TODO delete print statement later
						System.out.printf("CarSimulatorManagerCriticalSection:%s%n", car.toString());
						testingSpace.getRoot().getChildren().add(car.getCollisionShapeCar());

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
						testingSpace.getRoot().getChildren().remove(car.getCollisionShapeCar());
						car.setIsInitializedOnScreen(false);
						numberOfCarsVisibleOnScreen--;

						if (numberOfCarsVisibleOnScreen == 0) {
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
		TrafficLight[] trafficLights = new TrafficLight[] { new TrafficLight(new Point2D(220, 35), this, 10, 2, 12),
				new TrafficLight(new Point2D(490, 35), this, 10, 2, 12),
				new TrafficLight(new Point2D(770, 35), this, 10, 2, 12) };
		return trafficLights;
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
			double eastBoundCarPlacement = -2000;
			double westBoundCarPlacement = 3000;
			while (eastBoundCarPlacement < 0) {
				// Car is 75 pixels, add 80 to prevent pixels from causing an
				// IllegalArgumentException for overlapping cars
				eastBoundCarPlacement += random.nextDouble(80, 180);
				eastBoundCarXValues.add(eastBoundCarPlacement);
			}
			while (westBoundCarPlacement > 1000) {
				westBoundCarPlacement -= random.nextDouble(80, 180);
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

	private ArrayList<Car> createTestingCars() {
		ArrayList<Car> cars = new ArrayList<Car>();

		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
		cars.add(new Car(new Point2D(180, 160), Color.PINK, Velocity.EAST_TEN_MPH, this));
//
//		cars.add(new Car(new Point2D(370, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(470, 160), Color.BLACK, Velocity.EAST_FIVE_MPH, this));
//
//		cars.add(new Car(new Point2D(580, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(700, 160), Color.BLACK, Velocity.EAST_FIVE_MPH, this));
//
//		cars.add(new Car(new Point2D(830, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(1000, 160), Color.BLACK, Velocity.EAST_FIVE_MPH, this));
//
//		cars.add(new Car(new Point2D(100, 120), Color.BLACK, Velocity.WEST_FIVE_MPH, this));
//		cars.add(new Car(new Point2D(180, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
//
//		cars.add(new Car(new Point2D(370, 120), Color.BLACK, Velocity.WEST_FIVE_MPH, this));
//		cars.add(new Car(new Point2D(470, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
//
//		cars.add(new Car(new Point2D(580, 120), Color.BLACK, Velocity.WEST_FIVE_MPH, this));
//		cars.add(new Car(new Point2D(700, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
//
//		cars.add(new Car(new Point2D(830, 120), Color.BLACK, Velocity.WEST_FIVE_MPH, this));
//		cars.add(new Car(new Point2D(1000, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));

		return cars;
	}

	private TrafficLight[] createTestingTrafficLights() {
		TrafficLight[] trafficLights = new TrafficLight[] { new TrafficLight(new Point2D(220, 35), this, 1, 1, 1),
				new TrafficLight(new Point2D(490, 35), this, 1, 1, 1),
				new TrafficLight(new Point2D(770, 35), this, 1, 1, 1) };
		return trafficLights;
	}

}