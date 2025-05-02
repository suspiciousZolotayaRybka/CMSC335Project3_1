package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * CarProducer.java
 * Isaac Finehout
 * 30 April 2025
 *
 * This is the active producer object that creates cars to drive on the roads in
 * Project 3
 * @author fineh
 *
 */

public class CarProducer implements Runnable {

	private final Autopilot autopilot;
	// TODO verify this is the best data structure
	// Queue FIFO may be more efficient

	/**
	 * @param ap   the autopilot object, the passive concurrency class that controls
	 *             critical sections of code.
	 * @param cars the array list for cars in the simulation
	 */
	public CarProducer(Autopilot ap) {
		autopilot = ap;
	}

	/**
	 * The run method for the CarProducer, active class in the concurrency project 3
	 */
	@Override
	public void run() {
		try {
			// TODO anbother for loop here or in autopilot with a loop that moves cars??
			for (Car car : autopilot.getCars()) {
				System.out.println("Car placed in sim at: " + car.getPositionCar().toString());// TODO delete
				Thread.sleep(1000);
				autopilot.putCarInSimulationFullSpeed(car);
			}
		} catch (InterruptedException ie) {
			System.out.println("Interrupted Exception in CarProducer.java, run() method. Stack Trace below");
			ie.printStackTrace();
		}
		// TODO delete
		System.out.println("Producer Finished");
	}

	/**
	 *
	 * @return the autopilot
	 */
	public Autopilot getAutopilot() {
		return autopilot;
	}

}
