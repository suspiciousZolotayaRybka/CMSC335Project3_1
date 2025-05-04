package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Explosion.java
 * Isaac Finehout
 * 23 April 2025
 *
 * This is the class that creates runnable cars Project 3
 * @author fineh
 *
 *	Car's X polygon is 75 pixels in length. When an object reaches this length horizontally, they intersect.
 *  Head on is 157 -> The measurement is in the back left of the car: Note, verify this
 *
 */

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Car {

	// Declare variables
	private Point2D positionCar;
	private Polygon collisionShapeCar;
	private Color colorCar;
	private Velocity velocityCar;
	private boolean isInitializedOnScreen = false;
	private final int carID;
	private static int carIDCount = 0;

	/**
	 * Car constructor
	 *
	 * @param positionCar          Car's position x y coordinates
	 * @param collisionShapeCar    cars collision shape
	 * @param colorCar             color of the car
	 * @param speedMilesPerHourCar speed car is travelling
	 * @param directionCar         direction (north or south) car is travelling
	 */
	public Car(Point2D positionCar, Color colorCar, Velocity velocityCar) {
		this.positionCar = positionCar;
		this.colorCar = colorCar;
		this.velocityCar = velocityCar;
		carID = carIDCount++;
		updateCollisionShapeCar();
	}

	/**
	 * Assign border information to the collision shape according to Car's Point2D
	 */
	public void updateCollisionShapeCar() {
		collisionShapeCar = new Polygon();
		collisionShapeCar.setFill(colorCar);

		// If the car is going east, its collision shape will face that way.
		// If the car is going west, its collision shape will face that way.
		if (velocityCar.getDirection() == Direction.EAST) {
			collisionShapeCar.getPoints().addAll(new Double[] {
			//@formatter:off
			positionCar.getX(), positionCar.getY() + 20,               // bottom left (front bumper)
			positionCar.getX() + 10, positionCar.getY() + 10,          // hood front
			positionCar.getX() + 20, positionCar.getY() + 10,          // hood back
			positionCar.getX() + 25, positionCar.getY(),               // windshield front
			positionCar.getX() + 45, positionCar.getY(),               // roof front
			positionCar.getX() + 55, positionCar.getY() + 10,          // rear window
			positionCar.getX() + 70, positionCar.getY() + 10,          // trunk top
			positionCar.getX() + 80, positionCar.getY() + 20,          // bottom right (rear bumper)

			// Rear wheel
			positionCar.getX() + 65, positionCar.getY() + 20,
			positionCar.getX() + 65, positionCar.getY() + 25,
			positionCar.getX() + 75, positionCar.getY() + 25,
			positionCar.getX() + 75, positionCar.getY() + 20,

			// Front wheel
			positionCar.getX() + 10, positionCar.getY() + 20,
			positionCar.getX() + 10, positionCar.getY() + 25,
			positionCar.getX() + 20, positionCar.getY() + 25,
			positionCar.getX() + 20, positionCar.getY() + 20,

			positionCar.getX(), positionCar.getY() + 20                // close shape
			//@formatter:on

			});
		} else if (velocityCar.getDirection() == Direction.WEST) {
			collisionShapeCar.getPoints().addAll(new Double[] {
			//@formatter:off
			positionCar.getX(), positionCar.getY() + 20,               // bottom left (front bumper)
			positionCar.getX() - 10, positionCar.getY() + 10,          // hood front
			positionCar.getX() - 20, positionCar.getY() + 10,          // hood back
			positionCar.getX() - 25, positionCar.getY(),               // windshield front
			positionCar.getX() - 45, positionCar.getY(),               // roof front
			positionCar.getX() - 55, positionCar.getY() + 10,          // rear window
			positionCar.getX() - 70, positionCar.getY() + 10,          // trunk top
			positionCar.getX() - 80, positionCar.getY() + 20,          // bottom right (rear bumper)

			// Rear wheel
			positionCar.getX() - 65, positionCar.getY() + 20,
			positionCar.getX() - 65, positionCar.getY() + 25,
			positionCar.getX() - 75, positionCar.getY() + 25,
			positionCar.getX() - 75, positionCar.getY() + 20,

			// Front wheel
			positionCar.getX() - 10, positionCar.getY() + 20,
			positionCar.getX() - 10, positionCar.getY() + 25,
			positionCar.getX() - 20, positionCar.getY() + 25,
			positionCar.getX() - 20, positionCar.getY() + 20,

			positionCar.getX(), positionCar.getY() + 20                // close shape
			//@formatter:on

			});
		}

	}

	/**
	 * @return the positionCar
	 */
	public Point2D getPositionCar() {
		return positionCar;
	}

	/**
	 * @return the collisionShapeCar
	 */
	public Polygon getCollisionShapeCar() {
		return collisionShapeCar;
	}

	/**
	 * @return the colorCar
	 */
	public Color getColorCar() {
		return colorCar;
	}

	/**
	 * @return the velocityCar
	 */
	public Velocity getVelocityCar() {
		return velocityCar;
	}

	/**
	 * @return the isInitializedOnScreen
	 */
	public boolean getIsInitializedOnScreen() {
		return isInitializedOnScreen;
	}

	/**
	 * @param positionCar the positionCar to set
	 */
	public void setPositionCar(Point2D positionCar) {
		this.positionCar = positionCar;
		// Update the car's collision object
	}

	/**
	 * @param collisionShapeCar the collisionShapeCar to set
	 */
	public void setCollisionShapeCar(Polygon collisionShapeCar) {
		this.collisionShapeCar = collisionShapeCar;
	}

	/**
	 * @param colorCar the colorCar to set
	 */
	public void setColorCar(Color colorCar) {
		this.colorCar = colorCar;
	}

	/**
	 * @param velocityCar the velocityCar to set
	 */
	public void setDirectionCar(Velocity velocityCar) {
		this.velocityCar = velocityCar;
	}

	/**
	 *
	 * @param isInitializedOnScreen the isInitializedOnScreen to set
	 */
	public void setIsInitializedOnScreen(boolean isInitializedOnScreen) {
		this.isInitializedOnScreen = isInitializedOnScreen;
	}

	/**
	 *
	 * This method checks if a car collides with another car and returns a boolean
	 * value based on this condition
	 *
	 * @param car_j
	 * @return
	 */
	public boolean collidesWith(Car car_j) {
		return false;
	}

	/**
	 * Car String used for testing purposes
	 */
	@Override
	public String toString() {
		return String.format("Car#%d:position=%s,color=%s", carID, positionCar.toString(), colorCar.toString());
	}

}
