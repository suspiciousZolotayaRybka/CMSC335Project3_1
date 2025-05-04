package cmsc335project3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.geometry.Point2D;
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
//		cars = this.createRandomizedCars();
		cars = this.createTestingCars();
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

			checkForCollisions(car);

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

	public void checkForCollisions(Car car) {

		// Check for collisions with other cars, and make them explode
		ArrayList<Car> carCollisions = new ArrayList<Car>();

		Platform.runLater(() -> {

			try {
				carSimulationManagerLock.lock();

				for (Car car_j : cars) {
					if ((car != car_j) && (car.getIsInitializedOnScreen())) {
						Shape intersection = Shape.intersect(car.getCollisionShapeCar(), car_j.getCollisionShapeCar());
						if ((intersection.getBoundsInLocal().getWidth() > 0)
								&& (intersection.getBoundsInLocal().getHeight() > 0)) {
							System.out.println("BOOM");
							carCollisions.add(car);
							carCollisions.add(car_j);
							double carX = car.getPositionCar().getX();
							double car_jX = car_j.getPositionCar().getX();
							System.out.printf("X car 0:%f%nX car 1:%f%nDifference%f%n%n", carX, car_jX, car_jX - carX);
						}
					}
				}

				// Remove cars outside loop
				// TODO use a better algorithm than a nested for loop for time complexity
				if (!(carCollisions.isEmpty())) {
					System.out.println("BOOMNOT EMPTY");
					for (int i = 0; i < carCollisions.size(); i++) {
						testingSpace.getRoot().getChildren().remove(carCollisions.get(i).getCollisionShapeCar());
						carCollisions.get(i).setIsInitializedOnScreen(false);
						numberOfCarsVisibleOnScreen--;
						// TODO if numberOfCarsVisibleOnScreen reaches 0, end the simulation
						// TODO find a better place to do this?
						if (numberOfCarsVisibleOnScreen == 0) {
							isSimulationRunning = false;
						}
						System.out.println("numberOfCarsVisibleOnScreen=" + numberOfCarsVisibleOnScreen);

					}
				}

			} finally {
				// Unlock after the GUI update
				carSimulationManagerLock.unlock();
			}

		});

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
					carSimulationManagerLock.unlock();
				}
			});

			// Only signal the mover to start once the producer has finished
			if (carSimulationManagerLock.hasWaiters(needAllCarsProduced)
					&& (numberOfCarsVisibleOnScreen == cars.size())) {
				needAllCarsProduced.signal();
			}
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

		try {
			carSimulationManagerLock.lock();

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
				case 2 ->
					velocity = (direction == Direction.EAST ? Velocity.EAST_FIFTEEN_MPH : Velocity.WEST_FIFTEEN_MPH);
				}

				point2D = new Point2D(random.nextDouble(100, 900), direction == Direction.EAST ? 160 : 120);

				color = new Color(random.nextDouble(1.0), random.nextDouble(1.0), random.nextDouble(1.0), 1.0);

				cars.add(new Car(point2D, color, velocity));
			}

		} finally {
			carSimulationManagerLock.unlock();
		}

		return cars;
	}

	private ArrayList<Car> createTestingCars() {
		ArrayList<Car> cars = new ArrayList<Car>();

//		Car 0 Position:Point2D [x = 211.33583159925303, y = 160.0]
//				Car 1 Position:Point2D [x = 328.40155368399815, y = 120.0]
//				Car 2 Position:Point2D [x = 376.6097020710003, y = 160.0]
//				Car 3 Position:Point2D [x = 876.2311643425588, y = 120.0]
//				Car 4 Position:Point2D [x = 756.1500180015739, y = 160.0]
//				Car 5 Position:Point2D [x = 356.2754489258392, y = 120.0]
//				Car 6 Position:Point2D [x = 779.088915537401, y = 160.0]

		cars.add(new Car(new Point2D(211.33583159925303, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH));
		cars.add(new Car(new Point2D(328.40155368399815, 120), Color.RED, Velocity.WEST_FIFTEEN_MPH));
		cars.add(new Car(new Point2D(376.6097020710003, 160), Color.YELLOW, Velocity.EAST_FIFTEEN_MPH));
		cars.add(new Car(new Point2D(876.2311643425588, 120), Color.PINK, Velocity.WEST_FIFTEEN_MPH));
		cars.add(new Car(new Point2D(756.1500180015739, 160), Color.BLUE, Velocity.EAST_FIFTEEN_MPH));
		cars.add(new Car(new Point2D(356.2754489258392, 120), Color.PURPLE, Velocity.WEST_FIFTEEN_MPH));
		cars.add(new Car(new Point2D(779.088915537401, 160), Color.ORANGE, Velocity.EAST_FIFTEEN_MPH));

//		cars.add(new Car(new Point2D(100, 160), Color.BLACK, Velocity.EAST_FIFTEEN_MPH));
//		cars.add(new Car(new Point2D(185, 160), Color.BLACK, Velocity.EAST_FIVE_MPH));
//
//		cars.add(new Car(new Point2D(900, 120), Color.BLACK, Velocity.WEST_FIFTEEN_MPH));
//		cars.add(new Car(new Point2D(775, 120), Color.BLACK, Velocity.WEST_FIVE_MPH));

		return cars;
	}

}