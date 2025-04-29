package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Explosion.java
 * Isaac Finehout
 * 14 April 2025
 *
 * This is an explosion that occurs when two objects in Project 3 collide
 * @author fineh
 *
 */

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Explosion implements Runnable {

	// Declare variables
	private final Point2D positionExplosion;
	private Polygon collisionShapeExplosion;

	/**
	 * Explosion constructor
	 *
	 * @param positionExplosion
	 */
	public Explosion(Point2D positionExplosion) {
		this.positionExplosion = positionExplosion;
		updateCollisionShapeExplosion();
	}

	/**
	 * Assign border information to the collision shape according to Explosion's
	 * Point2D
	 */
	public void updateCollisionShapeExplosion() {
		collisionShapeExplosion = new Polygon();
		collisionShapeExplosion.setFill(Color.ORANGE);

		double centerX = positionExplosion.getX();
		double centerY = positionExplosion.getY();
		double radius = 30;
		double innerRadius = 15;

		collisionShapeExplosion.getPoints().addAll(new Double[] {
			//@formatter:off
		        centerX + (radius * Math.cos((0 * Math.PI) / 4)),      centerY + (radius * Math.sin((0 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((0.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((0.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((1 * Math.PI) / 4)),      centerY + (radius * Math.sin((1 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((1.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((1.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((2 * Math.PI) / 4)),      centerY + (radius * Math.sin((2 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((2.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((2.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((3 * Math.PI) / 4)),      centerY + (radius * Math.sin((3 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((3.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((3.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((4 * Math.PI) / 4)),      centerY + (radius * Math.sin((4 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((4.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((4.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((5 * Math.PI) / 4)),      centerY + (radius * Math.sin((5 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((5.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((5.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((6 * Math.PI) / 4)),      centerY + (radius * Math.sin((6 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((6.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((6.5 * Math.PI) / 4)),

		        centerX + (radius * Math.cos((7 * Math.PI) / 4)),      centerY + (radius * Math.sin((7 * Math.PI) / 4)),
		        centerX + (innerRadius * Math.cos((7.5 * Math.PI) / 4)), centerY + (innerRadius * Math.sin((7.5 * Math.PI) / 4)),
			//@formatter:on
		});
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the positionExplosion
	 */
	public Point2D getPositionExplosion() {
		return positionExplosion;
	}

	/**
	 * @return the collisionShapeExplosion
	 */
	public Polygon getCollisionShapeExplosion() {
		return collisionShapeExplosion;
	}

	/**
	 * @param collisionShapeExplosion the collisionShapeExplosion to set
	 */
	public void setCollisionShapeExplosion(Polygon collisionShapeExplosion) {
		this.collisionShapeExplosion = collisionShapeExplosion;
	}

}
