package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Velocity.java
 * Isaac Finehout
 * 13 April 2025
 *
 * This is an Enum that tracks cars velocity in spans of 5 mph
 * @author fineh
 *
 */

public enum Velocity {

	WEST_SLOW(5, Direction.WEST), WEST_MID(10, Direction.WEST), WEST_FAST(15, Direction.WEST),
	EAST_SLOW(5, Direction.EAST), EAST_MID(10, Direction.EAST), EAST_FAST(15, Direction.EAST);

	private final double speed;
	private final Direction direction;

	Velocity(double speed, Direction direction) {
		this.speed = speed;
		this.direction = direction;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}
}
