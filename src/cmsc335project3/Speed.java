package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * Velocity.java
 * Isaac Finehout
 * 3 April 2025
 *
 * This is an Enum that tracks cars velocity in spans of 5 mph
 * @author fineh
 *
 */
public enum Speed {

	ZERO_MILES_PER_HOUR(0, 1), FIVE_MILES_PER_HOUR(5, 1), TEN_MILES_PER_HOUR(10, 1), FIFTEEN_MILES_PER_HOUR(15, 1);

	private double miles;
	private double hours;

	/**
	 *
	 * @param miles the miles in the speed
	 * @param hours the hours in the speed
	 */
	Speed(double miles, double hours) {
		this.miles = miles;
		this.hours = hours;
	}

	/**
	 *
	 * @return the miles
	 */
	public double getMiles() {
		return miles;
	}

	/**
	 *
	 * @return the hours
	 */
	public double getHours() {
		return hours;
	}
}
