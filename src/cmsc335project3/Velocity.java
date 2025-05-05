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

	//@formatter:off
	WEST_ZERO_MPH(Speed.ZERO_MILES_PER_HOUR, Direction.WEST),
	WEST_FIVE_MPH(Speed.FIVE_MILES_PER_HOUR, Direction.WEST),
	WEST_TEN_MPH(Speed.TEN_MILES_PER_HOUR, Direction.WEST),
	WEST_FIFTEEN_MPH(Speed.FIFTEEN_MILES_PER_HOUR, Direction.WEST),
	EAST_ZERO_MPH(Speed.ZERO_MILES_PER_HOUR, Direction.EAST),
	EAST_FIVE_MPH(Speed.FIVE_MILES_PER_HOUR, Direction.EAST),
	EAST_TEN_MPH(Speed.TEN_MILES_PER_HOUR, Direction.EAST),
	EAST_FIFTEEN_MPH(Speed.FIFTEEN_MILES_PER_HOUR, Direction.EAST);
	//@formatter:on

	private final Speed speed;
	private final Direction direction;

	/**
	 *
	 * @param speed     the speed for velocity
	 * @param direction the direction for velocity
	 */
	Velocity(Speed speed, Direction direction) {
		this.speed = speed;
		this.direction = direction;
	}

	/**
	 * @return the speed
	 */
	public Speed getSpeed() {
		return speed;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	public static Velocity from(Speed speed, Direction direction) {
		for (Velocity v : values()) {
			if ((v.speed == speed) && (v.direction == direction)) {
				return v;
			}
		}
		throw new IllegalArgumentException(
				"Velocity.java in from -> No Velocity for speed " + speed + " and direction " + direction);
	}
}
