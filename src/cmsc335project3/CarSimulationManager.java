package cmsc335project3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

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
	private int numberOfCarsVisibleOnScreen;

	/**
	 * Constructor for autopilot
	 */
	public CarSimulationManager(TestingSpace testingSpace) {
		this.testingSpace = testingSpace;

		// TODO make simulation according to customer requirements
		cars = this.createRandomizedCars();
//		cars = createTestingCars();
		numberOfCarsVisibleOnScreen = 0;
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
				double acceleration = 0.1 *
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
							testingSpace.getRoot().getChildren().add(car.getCollisionShapeCar());
						} finally {
							// Unlock the thread
							carSimulationManagerLock.unlock();
						}
					});
				}
			}
			// Check for and remove car collisions
			try {
				carSimulationManagerLock.lock();
				removeCollidingCars(findCollidingCars());
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
				} finally {
					// Check for car collisions and remove any right away
					removeCollidingCars(findCollidingCars());
					carSimulationManagerLock.unlock();
				}
			});

//			// TODO uncomment once collisions is finished
			// Only signal the mover to start once the producer has finished
			if (carSimulationManagerLock.hasWaiters(needAllCarsProduced) && (numberOfCarsVisibleOnScreen != 0)) {
				needAllCarsProduced.signal();
			}
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

					// TODO debug variable remove
					int count = 0;

					for (Car car : collidingCars) {
						count++;
						testingSpace.getRoot().getChildren().remove(car.getCollisionShapeCar());
						car.setIsInitializedOnScreen(false);
						numberOfCarsVisibleOnScreen--;

						// TODO delete comment Check and see if this is the first time running the
						// collisions method.
						// If it is, it's from the CarProducer, and the needAllCarsProduced condition
						// should be false
//						if

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
					// Only compare unique pairs, add 1 to i for starting index
					Car car_j = cars.get(j);
					if (!car_j.getIsInitializedOnScreen()) {
						// If the car is not initialized on the screen, skip this iteration
						continue;
					}

					if (car_i.collidesWith(car_j)) {
						collidingCars.add(car_i);
						collidingCars.add(car_j);

						// TODO test and see if you can change the above to directly remove the cars
						// instead
						// Optional debug TODO delete optional debug.
						System.out.printf("Collision: car %d with car %d%n", i, j);
					}
				}
			}

		} finally {
			carSimulationManagerLock.unlock();
		}
		return collidingCars;

	}

	public void takeCarFromSimulationAndDelete() {
		// TODO delete just to remove lastItem warning
//		delete this method?
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
			Direction direction = null;
			Point2D point2D = null;
			Color color = null;
			int numCars = random.nextInt(20, 25);

			for (int i = 0; i < numCars; i++) {

				if (random.nextBoolean()) {
					direction = Direction.EAST;
				} else {
					direction = Direction.WEST;
				}

				switch (random.nextInt(3)) {
				case 0 -> velocity = (direction == Direction.EAST ? Velocity.EAST_FIVE_MPH : Velocity.WEST_FIVE_MPH);
				case 1 -> velocity = (direction == Direction.EAST ? Velocity.EAST_TEN_MPH : Velocity.WEST_TEN_MPH);
				case 2 ->
					velocity = (direction == Direction.EAST ? Velocity.EAST_FIFTEEN_MPH : Velocity.WEST_FIFTEEN_MPH);
				}

				point2D = new Point2D(random.nextDouble(100, 900), direction == Direction.EAST ? 160 : 120);

				color = new Color(random.nextDouble(1.0), random.nextDouble(1.0), random.nextDouble(1.0), 1.0);

				randomizedCars.add(new Car(point2D, color, velocity, this));
			}

		} finally {
			carSimulationManagerLock.unlock();
		}

		return randomizedCars;
	}

	private ArrayList<Car> createTestingCars() {
		ArrayList<Car> cars = new ArrayList<Car>();
//
//		cars.add(new Car(new Point2D(211.33583159925303, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(328.40155368399815, 120), Color.RED, Velocity.WEST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(376.6097020710003, 160), Color.YELLOW, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(876.2311643425588, 120), Color.PINK, Velocity.WEST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(756.1500180015739, 160), Color.BLUE, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(356.2754489258392, 120), Color.PURPLE, Velocity.WEST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(779.088915537401, 160), Color.ORANGE, Velocity.EAST_FIFTEEN_MPH, this));

		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
		cars.add(new Car(new Point2D(120, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
		cars.add(new Car(new Point2D(220, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
		cars.add(new Car(new Point2D(240, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));

		cars.add(new Car(new Point2D(100, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
		cars.add(new Car(new Point2D(80, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
		cars.add(new Car(new Point2D(60, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(220, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(240, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));

//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIVE_MPH, this));
//
//		cars.add(new Car(new Point2D(900, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH, this));
//		cars.add(new Car(new Point2D(775, 120), Color.BLACK, Velocity.WEST_FIVE_MPH, this));

		return cars;
	}

}