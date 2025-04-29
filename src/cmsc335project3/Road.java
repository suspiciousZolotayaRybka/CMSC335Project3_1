package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Explosion.java
 * Isaac Finehout
 * 23 April 2025
 *
 * This is the road cars drive on and lights are placed on in Project 3
 * @author fineh
 *
 */

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Road implements Runnable/** TODO Remove Runnable implementation? */
{

	// TODO getters and setters or final?
	// Declare variables
	private final Point2D positionRoad;
	private final double lengthRoad;
	private Group collisionShapeRoad;

	/**
	 * Road constructor
	 *
	 * @param lengthRoad
	 * @param positionRoad
	 */
	public Road(double lengthRoad, Point2D positionRoad) {
		this.lengthRoad = lengthRoad;
		this.positionRoad = positionRoad;
		updateCollisionShapeRoad();
	}

//	TODO DELETE
//	public void updateCollisionShapeRoad() {
//
//		collisionShapeRoad = new Polygon();
//		double roadWidth = 80.0;
//
//		// Calculate the corners of the rectangle representing the road
//		double x = positionRoad.getX();
//		double y = positionRoad.getY();
//
//		// Define the four corners of the rectangle (road)
//		collisionShapeRoad.getPoints().addAll(x, y - (roadWidth / 2), // Top-left
//				x + lengthRoad, y - (roadWidth / 2), // Top-right
//				x + lengthRoad, y + (roadWidth / 2), // Bottom-right
//				x, y + (roadWidth / 2 // Bottom-left
//				));
//	}
	// TODO update road shapes to look more realistic

	/**
	 * Assign border information to the collision shape according to Road's Point2D
	 */
	public void updateCollisionShapeRoad() {
		collisionShapeRoad = new Group();
		double roadWidth = 100.0;
		double x = positionRoad.getX();
		double y = positionRoad.getY();

		// ---- Road Body ----
		Polygon roadBody = new Polygon(x, y - (roadWidth / 2), // Top-left
				x + lengthRoad, y - (roadWidth / 2), // Top-right
				x + lengthRoad, y + (roadWidth / 2), // Bottom-right
				x, y + (roadWidth / 2 // Bottom-left
				));
		roadBody.setFill(Color.DARKSLATEGRAY);
		roadBody.setStroke(Color.WHITE);
		roadBody.setStrokeWidth(2);

		collisionShapeRoad.getChildren().add(roadBody);

		// ---- Lane Markings (Dashed Center Line) ----
		double segmentLength = 20;
		double spacing = 20;
		double centerY = y;

		for (double i = 0; i < lengthRoad; i += segmentLength + spacing) {
			Line lane = new Line(x + i, centerY, x + i + segmentLength, centerY);
			lane.setStroke(Color.WHITE);
			lane.setStrokeWidth(2);
			collisionShapeRoad.getChildren().add(lane);
		}

		// ---- Trees & Shrubs ----
		double treeSpacing = 130;
		double treeOffset = (roadWidth / 2) + 20;
		for (double i = 0; i < lengthRoad; i += treeSpacing) {
			double baseX = x + i;

			Circle treeLeft = new Circle(baseX, y - treeOffset, 8, Color.FORESTGREEN);
			Circle treeRight = new Circle(baseX, y + treeOffset, 8, Color.DARKGREEN);

			collisionShapeRoad.getChildren().addAll(treeLeft, treeRight);
		}

		// ---- Streetlights (Alternating Sides) ----
		double lightInterval = 100;
		for (double i = 0; i < lengthRoad; i += lightInterval) {
			double baseX = x + i;
			double side = ((i / lightInterval) % 2) == 0 ? -1 : 1;

			Line pole = new Line(baseX, y + (side * (roadWidth / 2)), baseX, y + (side * ((roadWidth / 2) + 25)));
			pole.setStroke(Color.GRAY);
			pole.setStrokeWidth(3);

			Circle bulb = new Circle(baseX, y + (side * ((roadWidth / 2) + 25)), 5);
			bulb.setFill(Color.GOLD);

			collisionShapeRoad.getChildren().addAll(pole, bulb);
		}
	}

	/**
	 * @return the collisionShapeRoad
	 */
	public Group getCollisionShapeRoad() {
		return collisionShapeRoad;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
