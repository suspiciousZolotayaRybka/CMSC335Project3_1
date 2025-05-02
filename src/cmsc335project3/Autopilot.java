package cmsc335project3;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javafx.scene.layout.Pane;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Explosion.java
 * Isaac Finehout
 * 23 April 2025
 *
 * This is the passive object that controls all active objects
 * @author fineh
 *
 */

public class Autopilot {

	private final ReentrantLock autopilotLock = new ReentrantLock();
	private final Condition carOnScreen = autopilotLock.newCondition();
	private final Condition needCar = autopilotLock.newCondition();
	private final Condition needEmptyCarSpace = autopilotLock.newCondition();

	// TODO change to better data structure
	private final ArrayList<Car> cars;
	private int lastItem;
	private int numberOfItems;
	private final Pane root;

	/**
	 *
	 * @param cars the cars to set
	 */
	public Autopilot(ArrayList<Car> cars, Pane root) {
		this.root = root;
		this.cars = cars;
		numberOfItems = 0;
		lastItem = 0;
	}

	public void putCarInSimulationFullSpeed(Car car) {

		try {
			autopilotLock.lock();
			while (numberOfItems >= cars.size()) {
				needEmptyCarSpace.await();
			}

			// TODO critical section of updating pane
			// TODO add car then call synchronized method or something here that loops to
			// move the car? then signals a waiter if the car reaches edge of screen?
			root.getChildren().add(car.getCollisionShapeCar());
			lastItem = (++lastItem % cars.size());
			numberOfItems++;

			if (autopilotLock.hasWaiters(needCar)) {
				needCar.signal();
			}

		} catch (InterruptedException ie) {
			System.out.println(
					"Interrupted Exception in Autopilot.java, putCarInSimulationFullSpeed(Car car) method. Stack Trace below");
			ie.printStackTrace();
		}

	}

	public void takeCarFromSimulationAndDelete() {

	}

}

//public static void updateRoot() {
//
//	try {
//		autopilotLock.lock();
//		cars = TestingSpace.getCars();
//
//		Thread.sleep(2000);
//		TestingSpace.getRoot().getChildren().clear();
//		TestingSpace.getRoot().getChildren().addAll(TestingSpace.getRoad().getCollisionShapeRoad(),
//				cars.get(0).getCollisionShapeCar());
//
//	} catch (InterruptedException ie) {
//	} finally {
//		autopilotLock.unlock();
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