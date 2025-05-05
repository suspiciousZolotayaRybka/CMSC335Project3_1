package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * TrafficLight.java
 * Isaac Finehout
 * 5 May 2025 2025
 *
 * This is the active object that controls a trafficlight
 * @author fineh
 *
 */

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TrafficLight implements Runnable {

	// Declare variables
	private final Point2D positionTrafficLight;
	private Circle indicatorTrafficLight;
	private Rectangle collisionRadiusTrafficLight;
	private Color colorTrafficLight = Color.GREEN;
	private TrafficLightColor tlc = TrafficLightColor.GREEN;
	private final int greenTimer;
	private final int yellowTimer;
	private final int redTimer;
	private final CarSimulationManager carSimulationManager;

	/**
	 * Constructor for traffic light
	 *
	 * @param the positionTrafficLight to set
	 */
	public TrafficLight(Point2D positionTrafficLight, CarSimulationManager carSimulationManager, int greenTimer,
			int yellowTimer, int redTimer) {
		this.positionTrafficLight = positionTrafficLight;
		this.carSimulationManager = carSimulationManager;
		this.greenTimer = greenTimer;
		this.yellowTimer = yellowTimer;
		this.redTimer = redTimer;
		updateIndicatorTrafficLight();
		updateCollisionRadiusTrafficLight();
	}

	@Override
	public void run() {
		while (carSimulationManager.isSimulationRunning()) {
			try {
				switch (tlc) {
				case GREEN -> Thread.sleep(greenTimer * 1000);
				case YELLOW -> Thread.sleep(yellowTimer * 1000);
				case RED -> Thread.sleep(redTimer * 1000);
				}
				carSimulationManager.updateTrafficLight(this);
			} catch (InterruptedException ie) {
				System.out.println("Interrupted Exception in TrafficLight.java, run() method. Stack Trace below");
				ie.printStackTrace();
			}
		}
	}

	/**
	 * Update the indicator for the traffic light, which is a circle representing
	 * the color
	 */
	public void updateIndicatorTrafficLight() {
		indicatorTrafficLight = new Circle();
		indicatorTrafficLight.setCenterX(positionTrafficLight.getX());
		indicatorTrafficLight.setCenterY(positionTrafficLight.getY());
		indicatorTrafficLight.setRadius(30);
		indicatorTrafficLight.setFill(colorTrafficLight);
	}

	/**
	 * Update the collisioRadius for the traffic light, which is a rectangle
	 * representing the area that cars respect to stop and go
	 */
	public void updateCollisionRadiusTrafficLight() {
		// Create the traffic light's collision radiuses
		collisionRadiusTrafficLight = new Rectangle(positionTrafficLight.getX() - 38, positionTrafficLight.getY(), 75,
				250);
	}

	/**
	 * TODO delete method?
	 *
	 * @return
	 */
	public Rectangle getCollisionRadiusTrafficLight() {
		return collisionRadiusTrafficLight;
	}

	/**
	 * @return the indicatorTrafficLight
	 */
	public Circle getIndicatorTrafficLight() {
		return indicatorTrafficLight;
	}

	/**
	 * @return the colorTrafficLight
	 */
	public Color getColorTrafficLight() {
		return colorTrafficLight;
	}

	/**
	 * @param colorTrafficLight the colorTrafficLight to set
	 */
	public void setColorTrafficLight(Color colorTrafficLight) {
		this.colorTrafficLight = colorTrafficLight;
		if (colorTrafficLight == Color.GREEN) {
			tlc = TrafficLightColor.GREEN;
		} else if (colorTrafficLight == Color.RED) {
			tlc = TrafficLightColor.RED;
		} else if (colorTrafficLight == Color.YELLOW) {
			tlc = TrafficLightColor.YELLOW;
		}
		indicatorTrafficLight.setFill(this.colorTrafficLight);
	}

	/**
	 * @return the tlc
	 */
	public TrafficLightColor getTlc() {
		return tlc;
	}

}
