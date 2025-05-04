package cmsc335project3;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
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

public class CarSimulationManager {

	// Concurrency items
	private final ReentrantLock carSimulationManagerLock = new ReentrantLock();
//	private final Condition carOnScreen = carSimulationManagerLock.newCondition(); TODO delete?
	private final Condition needCar = carSimulationManagerLock.newCondition();
	private final Condition needEmptyCarSpace = carSimulationManagerLock.newCondition();
//	private final boolean isSimulationRunning = true;  TODO delete?

	// TODO change to better data structure
	private final ArrayList<Car> cars = new ArrayList<Car>();
	private final Road road = new Road(1000, new Point2D(0, 150));
	private final int lastItem;
	private final int numberOfItems;

	/**
	 * Constructor for autopilot
	 */
	public CarSimulationManager() {
		// TODO randomize cars made each time
		cars.add(startCarInd0());
		cars.add(startCarInd1());
		numberOfItems = 0;
		lastItem = 0;
	}

	public void putCarInSimulationFullSpeed(Car car) {

		try {
			carSimulationManagerLock.lock();
			while (numberOfItems >= cars.size()) {
				needEmptyCarSpace.await();
			}

			// TODO critical section of updating pane
			// TODO add car then call synchronized method or something here that loops to
			// move the car? then signals a waiter if the car reaches edge of screen?
//			root.getChildren().add(car.getCollisionShapeCar());
//			lastItem = (++lastItem % cars.size());
//			numberOfItems++;

			if (carSimulationManagerLock.hasWaiters(needCar)) {
				needCar.signal();
			}

		} catch (InterruptedException ie) {
			System.out.println(
					"Interrupted Exception in CarSimulationManager.java, putCarInSimulationFullSpeed(Car car) method. Stack Trace below");
			ie.printStackTrace();
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

	private static Car startCarInd0() {
		Point2D point2D = new Point2D(0, 175);
		Color color = Color.RED;
		Velocity velocity = Velocity.EAST_SLOW;
		return new Car(point2D, color, velocity);
	}

	private static Car startCarInd1() {
		Point2D point2D = new Point2D(180, 125);
		Color color = Color.BLUE;
		Velocity velocity = Velocity.WEST_SLOW;
		return new Car(point2D, color, velocity);
	}

}

//public static void updateRoot() {
//
//	try {
//		carSimulationManagerLock.lock();
//		cars = TestingSpace.getCars();
//
//		Thread.sleep(2000);
//		TestingSpace.getRoot().getChildren().clear();
//		TestingSpace.getRoot().getChildren().addAll(TestingSpace.getRoad().getCollisionShapeRoad(),
//				cars.get(0).getCollisionShapeCar());
//
//	} catch (InterruptedException ie) {
//	} finally {
//		carSimulationManagerLock.unlock();
//	}
//
//}

//@Override
//public void run() {
//	// TODO make car drive in one direction
//	while (isOnScreen) {
//		// @formatter:off
//		double newX = velocityCar.getDirection() == Direction.EAST ?
//						positionCar.getX() + velocityCar.getSpeed()
//						:
//							// Else direction is West
//						positionCar.getX() - velocityCar.getSpeed();
//		// @formatter:on
//		positionCar = new Point2D(newX, positionCar.getY());
//		System.out.println(positionCar);
//		updateCollisionShapeCar();
//
//		// TODO DELETE THIS MOVE TO CRITICAL SECTION OF PASSIVE OBJECT
//		if ((positionCar.getX() > 1000) || (positionCar.getX() < 0)) {
//			isOnScreen = false;
//		}
//		Autopilot.updateRoot();
//		// TODO DELETE THIS MOVE TO CRITICAL SECTION OF PASSIVE OBJECT
//	}
//
//}