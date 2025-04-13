package cmsc335project3;

import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Car implements Runnable {

	// Declare variables
	private Point positionCar;
	private Polygon collisionShapeCar;
	private Color colorCar;
	private double speedMilesPerHourCar;
	private Direction directionCar;

	/**
	 * Car constructor
	 *
	 * @param positionCar          Car's position x y coordinates
	 * @param collisionShapeCar    cars collision shape
	 * @param colorCar             color of the car
	 * @param speedMilesPerHourCar speed car is travelling
	 * @param directionCar         direction (north or south) car is travelling
	 */
	public Car(Point positionCar, Polygon collisionShapeCar, Color colorCar, double speedMilesPerHourCar,
			Direction directionCar) {
		this.positionCar = positionCar;
		this.collisionShapeCar = collisionShapeCar;
		this.colorCar = colorCar;
		this.speedMilesPerHourCar = speedMilesPerHourCar;
		this.directionCar = directionCar;

	}

	@Override
	public void run() {
		System.out.println("Car is running");

	}

	/**
	 * @return the positionCar
	 */
	public Point getPositionCar() {
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
	 * @return the speedMilesPerHourCar
	 */
	public double getSpeedMilesPerHourCar() {
		return speedMilesPerHourCar;
	}

	/**
	 * @return the directionCar
	 */
	public Direction getDirectionCar() {
		return directionCar;
	}

	/**
	 * @param positionCar the positionCar to set
	 */
	public void setPositionCar(Point positionCar) {
		this.positionCar = positionCar;
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
	 * @param speedMilesPerHourCar the speedMilesPerHourCar to set
	 */
	public void setSpeedMilesPerHourCar(double speedMilesPerHourCar) {
		this.speedMilesPerHourCar = speedMilesPerHourCar;
	}

	/**
	 * @param directionCar the directionCar to set
	 */
	public void setDirectionCar(Direction directionCar) {
		this.directionCar = directionCar;
	}

}
