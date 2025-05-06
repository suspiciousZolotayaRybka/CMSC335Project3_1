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
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

public class Car {

	// Declare variables
	private Point2D positionCar;
	private Polygon collisionShapeCar;
	private Rectangle collisionRadiusCar;
	private Rectangle collisionRadiusForTrafficLight;
	private Color colorCar;
	private Velocity velocityCar;
	private final Speed preferredSpeed;
	private boolean isInitializedOnScreen = false;
	private final CarSimulationManager carSimulationManager;
	private final int carID;
	private static int carIDCount = 0;
	private static final int carWidth = 80;
	private static final int carHeight = 25;
	private Label carLabel;

	/**
	 * Car constructor
	 *
	 * @param positionCar          Car's position x y coordinates
	 * @param collisionShapeCar    cars collision shape
	 * @param colorCar             color of the car
	 * @param speedMilesPerHourCar speed car is travelling
	 * @param directionCar         direction (north or south) car is travelling
	 */
	public Car(Point2D positionCar, Color colorCar, Velocity velocityCar, CarSimulationManager carSimulationManager) {
		this.positionCar = positionCar;
		this.colorCar = colorCar;
		this.velocityCar = velocityCar;
		preferredSpeed = velocityCar.getSpeed();
		this.carSimulationManager = carSimulationManager;
		carID = carIDCount++;
		carLabel = new Label();
		carLabel.setFont(Font.font("Roboto", 20));
		updateCarLabel();
		updateCollisionShapeCar();
		updateCollisionRadius();
		updateCollisionRadiusForTrafficLight();
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
	 * Update the collisionRadius of the car
	 */
	public void updateCollisionRadius() {
		// Create the car's collision radius
		final double xPadding = 30;
		final double yPadding = 5;

		if (velocityCar.getDirection() == Direction.EAST) {
			// Make an east bound collision radius
			collisionRadiusCar = new Rectangle(positionCar.getX() - xPadding, positionCar.getY() - yPadding,
					carWidth + (2 * xPadding), carHeight + (2 * yPadding));
		} else {
			// Make a west bound collision radius
			collisionRadiusCar = new Rectangle(positionCar.getX() - 110, positionCar.getY() - yPadding,
					carWidth + (2 * xPadding), carHeight + (2 * yPadding));
		}

	}

	/**
	 * The collisionRadiusForTrafficLight is identical to car collision radius
	 * except only 5 pixels large, so cars don't stall in intersections
	 */
	public void updateCollisionRadiusForTrafficLight() {
		// Create the car's collision radius for traffic lights
		// identical to car collision radius except only 5 pixels large, so cars don't
		// stall in intersections
		final double yPadding = 5;

		if (velocityCar.getDirection() == Direction.EAST) {
			// Make an east bound collision radius
			collisionRadiusForTrafficLight = new Rectangle(positionCar.getX() + 80, positionCar.getY() - yPadding, 5,
					carHeight + (2 * yPadding));
		} else {
			// Make a west bound collision radius
			collisionRadiusForTrafficLight = new Rectangle(positionCar.getX() - 90, positionCar.getY() - yPadding, 5,
					carHeight + (2 * yPadding));
		}
	}

	/**
	 *
	 * This method checks if a car's collision radius intersects with another
	 * collision radius and returns a boolean value based on this condition
	 *
	 * @param collisionRadiusCar
	 */
	public boolean isWithinCollisionRadius(Rectangle collisionRadius) {
		boolean isWithinCollisionRadius = false;

		if (collisionRadiusCar == collisionRadius) {
			// Do nothing. it is the same car's collision radius
			isWithinCollisionRadius = false;
		} else if (this.getIsInitializedOnScreen()) {
			// Ensure the car is on the screen
			// Create and test the intersection
			Shape intersection = Shape.intersect(collisionRadiusCar, collisionRadius);
			if ((intersection.getBoundsInLocal().getWidth() > 0) && (intersection.getBoundsInLocal().getHeight() > 0)) {
				// If the two cars collide, return true
				isWithinCollisionRadius = true;
			}
		}

		return isWithinCollisionRadius;
	}

	/**
	 *
	 * This method checks if a car collides with another car and returns a boolean
	 * value based on this condition
	 *
	 * @param car_j
	 * @return
	 */
	public boolean collidesWithCar(Car car_j) {
		boolean collidesWithOtherCar = false;

		if (this == car_j) {
			// Do nothing. it is the same car
			collidesWithOtherCar = false;
		} else if (this.getIsInitializedOnScreen() || carSimulationManager.isProducerProducing()) {
			// Check for collisions, the car is initialized on the screen

			// Also check for collisions if the producer is producing
			// The car will not be initialized on the screen in this case

			Shape intersection = Shape.intersect(collisionShapeCar, car_j.getCollisionShapeCar());
			if ((intersection.getBoundsInLocal().getWidth() > 0) && (intersection.getBoundsInLocal().getHeight() > 0)) {
				// If the two cars collide, return true
				collidesWithOtherCar = true;
			}
		}

		return collidesWithOtherCar;
	}

	/**
	 * Car String used for testing purposes
	 */
	@Override
	public String toString() {
		return String.format("Car#%d:isInitializedOnScreen=%s,color=%s,position=%s", carID, isInitializedOnScreen,
				colorCar.toString(), positionCar.toString());
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
	 * @return the collisionRadiusForTrafficLight
	 */
	public Rectangle getCollisionRadiusForTrafficLight() {
		return collisionRadiusForTrafficLight;
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
	 * @return the collisionRadiusCar
	 */
	public Rectangle getCollisionRadiusCar() {
		return collisionRadiusCar;
	}

	/**
	 *
	 * @return the preferredSpeed
	 */
	public Speed getPreferredSpeed() {
		return preferredSpeed;
	}

	/**
	 * @return the carLabel
	 */
	public Label getCarLabel() {
		return carLabel;
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
	 * @param the velocityCar to set
	 */
	public void setVelocityCar(Velocity velocityCar) {
		this.velocityCar = velocityCar;
	}

	/**
	 * Update the carLabel
	 */
	public void updateCarLabel() {
		// Change x for east and west bound cars
		carLabel = new Label(String.format("CarX=%.2f%nSpeed=%.0fmph", positionCar.getX(),
				velocityCar.getSpeed().getMiles() / velocityCar.getSpeed().getHours()));
		carLabel.setLayoutX(positionCar.getX() - (velocityCar.getDirection() == Direction.EAST ? 0 : 80));
		carLabel.setLayoutY(positionCar.getY() + (velocityCar.getDirection() == Direction.EAST ? +85 : -95));
	}
}