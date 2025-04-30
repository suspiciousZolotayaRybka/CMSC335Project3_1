package cmsc335project3;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

	private static final ReentrantLock autopilotLock = new ReentrantLock();
	private static final Condition carOnScreen = autopilotLock.newCondition();

	private static ArrayList<Car> cars;

	public static void updateRoot() {

		try {
			autopilotLock.lock();
			cars = TestingSpace.getCars();

			Thread.sleep(2000);
			TestingSpace.getRoot().getChildren().clear();
			TestingSpace.getRoot().getChildren().addAll(TestingSpace.getRoad().getCollisionShapeRoad(),
					cars.get(0).getCollisionShapeCar());

		} catch (InterruptedException ie) {
		} finally {
			autopilotLock.unlock();
		}

	}
}
